package org.opendaylight.yang.gen.v1.urn.honeycomb.params.xml.ns.yang.v3po2vpp.rev160406;

import io.fd.honeycomb.v3po.translate.impl.read.CompositeListReader;
import io.fd.honeycomb.v3po.translate.impl.read.CompositeRootReader;
import io.fd.honeycomb.v3po.translate.read.ChildReader;
import io.fd.honeycomb.v3po.translate.util.read.CloseableReader;
import io.fd.honeycomb.v3po.translate.util.read.ReflexiveRootReaderCustomizer;
import io.fd.honeycomb.v3po.translate.v3po.vppclassifier.ClassifySessionReader;
import io.fd.honeycomb.v3po.translate.v3po.vppclassifier.ClassifyTableReader;
import java.util.ArrayList;
import java.util.List;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpp.classifier.rev150603.VppClassifierState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpp.classifier.rev150603.VppClassifierStateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpp.classifier.rev150603.classify.table.base.attributes.ClassifySession;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpp.classifier.rev150603.classify.table.base.attributes.ClassifySessionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpp.classifier.rev150603.classify.table.base.attributes.ClassifySessionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpp.classifier.rev150603.vpp.classifier.state.ClassifyTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpp.classifier.rev150603.vpp.classifier.state.ClassifyTableBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpp.classifier.rev150603.vpp.classifier.state.ClassifyTableKey;
import org.opendaylight.yangtools.yang.binding.ChildOf;

public class VppClassifierStateHoneycombReaderModule extends org.opendaylight.yang.gen.v1.urn.honeycomb.params.xml.ns.yang.v3po2vpp.rev160406.AbstractVppClassifierStateHoneycombReaderModule {
    public VppClassifierStateHoneycombReaderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public VppClassifierStateHoneycombReaderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.honeycomb.params.xml.ns.yang.v3po2vpp.rev160406.VppClassifierStateHoneycombReaderModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final CompositeListReader<ClassifySession, ClassifySessionKey, ClassifySessionBuilder> classifySessionReader =
            new CompositeListReader<>(ClassifySession.class,
                new ClassifySessionReader(getVppJvppDependency(), getClassifyTableContextDependency()));

        final List<ChildReader<? extends ChildOf<ClassifyTable>>> classifyTableChildReaders = new ArrayList<>();
        classifyTableChildReaders.add((ChildReader)classifySessionReader);
        final CompositeListReader<ClassifyTable, ClassifyTableKey, ClassifyTableBuilder> classifyTableReader =
            new CompositeListReader<>(
                ClassifyTable.class,
                classifyTableChildReaders,
                new ClassifyTableReader(getVppJvppDependency(), getClassifyTableContextDependency()));

        final List<ChildReader<? extends ChildOf<VppClassifierState>>> vppClassifierStateChildReaders = new ArrayList<>();
        vppClassifierStateChildReaders.add(classifyTableReader);
        return new CloseableReader<>(new CompositeRootReader<>(
            VppClassifierState.class,
            vppClassifierStateChildReaders,
            new ReflexiveRootReaderCustomizer<>(VppClassifierStateBuilder.class)));
    }

}
