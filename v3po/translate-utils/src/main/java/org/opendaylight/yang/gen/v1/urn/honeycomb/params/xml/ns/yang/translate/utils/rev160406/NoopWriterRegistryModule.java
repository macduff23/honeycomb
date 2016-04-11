package org.opendaylight.yang.gen.v1.urn.honeycomb.params.xml.ns.yang.translate.utils.rev160406;

import io.fd.honeycomb.v3po.translate.util.write.*;
import io.fd.honeycomb.v3po.translate.util.write.NoopWriterRegistry;

public class NoopWriterRegistryModule extends org.opendaylight.yang.gen.v1.urn.honeycomb.params.xml.ns.yang.translate.utils.rev160406.AbstractNoopWriterRegistryModule {
    public NoopWriterRegistryModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public NoopWriterRegistryModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.honeycomb.params.xml.ns.yang.translate.utils.rev160406.NoopWriterRegistryModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        return new CloseableWriterRegistry(new NoopWriterRegistry());
    }

}