package io.fd.honeycomb.lisp.translate.write;


import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.common.base.Optional;
import io.fd.honeycomb.translate.write.WriteFailedException;
import io.fd.honeycomb.vpp.test.write.WriterCustomizerTest;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lisp.rev161214.eid.table.grouping.eid.table.VniTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lisp.rev161214.eid.table.grouping.eid.table.VniTableBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lisp.rev161214.eid.table.grouping.eid.table.vni.table.VrfSubtableBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class VniTableCustomizerTest extends WriterCustomizerTest {

    private VniTableCustomizer customizer;
    private InstanceIdentifier<VniTable> validId;
    private VniTable validData;

    @Before
    public void init() {
        initMocks(this);
        customizer = new VniTableCustomizer(api);

        validId = InstanceIdentifier.create(VniTable.class);
        validData = new VniTableBuilder()
                .setVrfSubtable(new VrfSubtableBuilder()
                        .build()).build();
    }

    @Test
    public void testWriteSuccessfull() {
        whenReadAfterReturnValid();
        try {
            customizer.writeCurrentAttributes(validId, validData, writeContext);
        } catch (Exception e) {
            fail("Test should pass without exception");
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testWriteFailed() throws WriteFailedException {
        whenReadAfterReturnInvalid();
        customizer.writeCurrentAttributes(validId, validData, writeContext);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUpdate() throws WriteFailedException {
        customizer.updateCurrentAttributes(validId, validData, validData, writeContext);
    }

    @Test
    public void testDeleteSuccessfull() {
        whenReadBeforeReturnValid();
        try {
            customizer.deleteCurrentAttributes(validId, validData, writeContext);
        } catch (Exception e) {
            fail("Test should pass without exception");
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteFailed() throws WriteFailedException {
        whenReadBeforeReturnInvalid();
        customizer.deleteCurrentAttributes(validId, validData, writeContext);
    }

    private void whenReadBeforeReturnValid() {
        when(writeContext.readBefore(validId)).thenReturn(Optional.of(validData));
    }

    private void whenReadBeforeReturnInvalid() {
        when(writeContext.readBefore(validId)).thenReturn(Optional.absent());
    }

    private void whenReadAfterReturnValid() {
        when(writeContext.readAfter(validId)).thenReturn(Optional.of(validData));
    }

    private void whenReadAfterReturnInvalid() {
        when(writeContext.readAfter(validId)).thenReturn(Optional.absent());
    }
}