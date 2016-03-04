/*
 * Copyright (c) 2015 Cisco and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fd.honeycomb.v3po.impl;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.iana._if.type.rev140508.EthernetCsmacd;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.iana._if.type.rev140508.SoftwareLoopback;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.interfaces.rev140508.InterfaceType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.interfaces.rev140508.Interfaces;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.interfaces.rev140508.InterfacesBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.interfaces.rev140508.InterfacesState;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.interfaces.rev140508.InterfacesStateBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.interfaces.rev140508.interfaces.Interface;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.interfaces.rev140508.interfaces.InterfaceBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.interfaces.rev140508.interfaces.InterfaceKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.v3po.rev150105.V3poService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.v3po.rev150105.Vpp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.v3po.rev150105.VppBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.v3po.rev150105.VxlanTunnel;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.openvpp.vppjapi.vppApi;
import org.openvpp.vppjapi.vppInterfaceDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V3poProvider implements BindingAwareProvider, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(V3poProvider.class);
    private RpcRegistration<V3poService> v3poService;
    private VppIetfInterfaceListener vppInterfaceListener;
    private VppBridgeDomainListener vppBridgeDomainListener;
    private vppApi api;
    private DataBroker db;
    VppPollOperDataImpl vppPollOperData;

    private void initializeVppConfig() {

        WriteTransaction transaction = db.newWriteOnlyTransaction();
        InstanceIdentifier<Vpp> viid = InstanceIdentifier.create(Vpp.class);
        Vpp vpp = new VppBuilder().build();
        // FIXME uncomment after ODL bug-5382 is fixed
        // transaction.put(LogicalDatastoreType.CONFIGURATION, viid, vpp);
        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        Futures.addCallback(future, new
                            LoggingFuturesCallBack<>("VPPCFG-WARNING: Failed to create Vpp "
                                                     + "configuration db.",
                                                     LOG));
        vppBridgeDomainListener = new VppBridgeDomainListener(db, api);

        LOG.info("VPPCFG-INFO: Preparing to initialize the IETF Interface " + "list configuration db.");
        transaction = db.newWriteOnlyTransaction();

        // FIXME this is minimal and we need to sync the entire configuration
        syncInterfaces(transaction, api);

        future = transaction.submit();
        Futures.addCallback(future, new
                            LoggingFuturesCallBack<>("VPPCFG-WARNING: Failed to create IETF "
                                                     + "Interface list configuration db.",
                                                     LOG));
        vppInterfaceListener = new VppIetfInterfaceListener(db, api);

    }

    private static final Map<String, Class<? extends InterfaceType>> IFC_TYPES =
        new HashMap<String, Class<? extends InterfaceType>>() {{
            put("vxlan", VxlanTunnel.class);
            put("lo", SoftwareLoopback.class);
            put("Ether", EthernetCsmacd.class);
            // TODO missing types below
            put("l2tpv3_tunnel", EthernetCsmacd.class);
            put("tap", EthernetCsmacd.class);
        }};

    /**
     * Dump all interfaces from VPP and populate config datastore to sync initial state (interfaces)
     * Only the mandatory leaves are stored in config datastore
     */
    private void syncInterfaces(final WriteTransaction transaction, final vppApi api) {
        LOG.info("Starting interface configuration sync");
        final List<Interface> ifcs = Lists.newArrayList();
        for (Map.Entry<String, Class<? extends InterfaceType>> ifcType : IFC_TYPES.entrySet()) {

            for (vppInterfaceDetails vppIfc : api.swInterfaceDump(((byte) 1), ifcType.getKey().getBytes())) {
                ifcs.add(new InterfaceBuilder()
                    .setName(vppIfc.interfaceName)
                    .setKey(new InterfaceKey(vppIfc.interfaceName))
                    .setEnabled(vppIfc.adminUp == 1)
                    .setType(ifcType.getValue())
                    .build());
            }
        }

        InstanceIdentifier<Interfaces> iid = InstanceIdentifier.create(Interfaces.class);
        transaction.put(LogicalDatastoreType.CONFIGURATION, iid, new InterfacesBuilder().setInterface(ifcs).build());
        LOG.info("Interface configuration sync ended with following interfaces: {}", ifcs);
    }

    /* operational data */

    private void initVppOperational() {
        /*
         * List<org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.
         * interfaces.rev140508.interfaces.state.Interface> ifaces = new
         * ArrayList<org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.
         * ietf.interfaces.rev140508.interfaces.state.Interface>();
         */
        LOG.info("VPPOPER-INFO: Preparing to initialize the IETF Interface " + "state list operational db.");
        InterfacesState ifsState = new InterfacesStateBuilder().build();
        WriteTransaction tx = db.newWriteOnlyTransaction();
        InstanceIdentifier<InterfacesState> isid = InstanceIdentifier.builder(InterfacesState.class).build();
        tx.put(LogicalDatastoreType.OPERATIONAL, isid, ifsState);
        Futures.addCallback(tx.submit(), new LoggingFuturesCallBack<>(
                "VPPOPER-WARNING: Failed to create IETF " + "Interface state list operational db.", LOG));
    }

    private void startOperationalUpdateTimer() {
        Timer timer = new Timer();

        // fire task after 1 second and then repeat each 10 seconds
        timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    vppPollOperData.updateOperational();
                }
            }, 1000, 10000);
    }

    @Override
    public void onSessionInitiated(final ProviderContext session) {
        LOG.info("VPP-INFO: V3poProvider Session Initiated");

        try {
	    api = new vppApi("v3poODL");
        } catch (IOException e) {
            LOG.error("VPP-ERROR: VPP api client connection failed", e);
            return;
        }

        LOG.info("VPP-INFO: VPP api client connection established");

        db = session.getSALService(DataBroker.class);
        initializeVppConfig();
        initVppOperational();

        vppPollOperData = new VppPollOperDataImpl(api, db);
        v3poService = session.addRpcImplementation(V3poService.class,
                                                   vppPollOperData);
        startOperationalUpdateTimer();
    }

    @Override
    public void close() throws Exception {
        LOG.info("VPP-INFO: V3poProvider Closed");
        if (v3poService != null) {
            v3poService.close();
        }
        if (api != null) {
            api.close();
        }
    }
}