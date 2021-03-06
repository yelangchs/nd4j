package org.nd4j.linalg.specials;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.nativeblas.NativeOpsHolder;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author raver119@gmail.com
 */
@Slf4j
@RunWith(Parameterized.class)
public class SortCooTests extends BaseNd4jTest {

    DataBuffer.Type initialType;

    public SortCooTests(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Before
    public void setUp() {
        Nd4j.setDataType(DataBuffer.Type.FLOAT);
    }

    @After
    public void setDown() {
        Nd4j.setDataType(initialType);
    }

    @Test
    public void sortSparseCooIndicesSort1() throws Exception {
        // FIXME: we don't want this test running on cuda for now
        if (Nd4j.getExecutioner().getClass().getCanonicalName().toLowerCase().contains("cuda"))
            return;

        int indices[] = new int[]{1, 0, 0,    0, 1, 1,   0, 1, 0,   1, 1, 1};

        // we don't care about
        double values[] = new double[]{2, 1, 0, 3};
        int expIndices[] = new int[]{0, 1, 0,    0, 1, 1,   1, 0, 0,   1, 1, 1};
        double expValues[] = new double[]{0, 1, 2, 3};

        DataBuffer idx = Nd4j.getDataBufferFactory().createInt(indices);
        DataBuffer val = Nd4j.createBuffer(values);

        log.info("Old indices: {}", Arrays.toString(idx.asInt()));

        NativeOpsHolder.getInstance().getDeviceNativeOps().sortCooIndicesFloat(null, (IntPointer) idx.addressPointer(), (FloatPointer) val.addressPointer(), 4, 3);


        log.info("New indices: {}", Arrays.toString(idx.asInt()));

        assertArrayEquals(expIndices, idx.asInt());
        assertArrayEquals(expValues, val.asDouble(), 1e-5);
    }

    @Override
    public char ordering() {
        return 'c';
    }
}
