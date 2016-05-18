package org.opendaylight.yang.gen.v1.urn.honeycomb.params.xml.ns.yang.vpp.data.init.rev160407;

import io.fd.honeycomb.v3po.vpp.data.init.InterfacesInitializer;

public class InterfacesConfigurationInitializerModule extends org.opendaylight.yang.gen.v1.urn.honeycomb.params.xml.ns.yang.vpp.data.init.rev160407.AbstractInterfacesConfigurationInitializerModule {
    public InterfacesConfigurationInitializerModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public InterfacesConfigurationInitializerModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.honeycomb.params.xml.ns.yang.vpp.data.init.rev160407.InterfacesConfigurationInitializerModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        return new InterfacesInitializer(getBindingDataBrokerDependency());
    }

}
