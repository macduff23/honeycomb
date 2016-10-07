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

package io.fd.honeycomb.lisp.translate.read.dump.executor;

import static com.google.common.base.Preconditions.checkNotNull;

import io.fd.honeycomb.lisp.translate.read.dump.executor.params.SubtableDumpParams;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.fd.honeycomb.translate.util.read.cache.EntityDumpExecutor;
import io.fd.honeycomb.translate.vpp.util.JvppReplyConsumer;
import io.fd.vpp.jvpp.core.dto.LispEidTableMapDetailsReplyDump;
import io.fd.vpp.jvpp.core.dto.LispEidTableMapDump;
import io.fd.vpp.jvpp.core.future.FutureJVppCore;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lisp.rev161214.eid.table.grouping.eid.table.vni.table.BridgeDomainSubtable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lisp.rev161214.eid.table.grouping.eid.table.vni.table.VrfSubtable;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

/**
 * Dump executor for {@link VrfSubtable}/{@link BridgeDomainSubtable}
 */
public final class SubtableDumpExecutor extends AbstractJvppDumpExecutor
        implements EntityDumpExecutor<LispEidTableMapDetailsReplyDump, SubtableDumpParams>, JvppReplyConsumer {

    public SubtableDumpExecutor(@Nonnull final FutureJVppCore vppApi) {
        super(vppApi);
    }

    @Override
    public LispEidTableMapDetailsReplyDump executeDump(final InstanceIdentifier<?> identifier,
                                                       final SubtableDumpParams params)
            throws ReadFailedException {
        LispEidTableMapDump request = new LispEidTableMapDump();
        request.isL2 = checkNotNull(params, "Cannot bind null params").isL2();

        return getReplyForRead(vppApi.lispEidTableMapDump(request).toCompletableFuture(), identifier);
    }

}
