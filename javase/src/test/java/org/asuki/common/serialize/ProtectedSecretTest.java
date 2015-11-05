package org.asuki.common.serialize;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.testng.annotations.Test;

public class ProtectedSecretTest {

    @Test
    public void testCipher() throws Exception {
        ProtectedSecret secret1 = new ProtectedSecret("password");
        ProtectedSecret secret2;
        byte[] serial;

        // serialize
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutput output = new ObjectOutputStream(baos)) {
            output.writeObject(secret1);
            output.flush();

            serial = baos.toByteArray();
        }

        // deserialize
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serial);
                ObjectInput input = new ObjectInputStream(bais)) {
            secret2 = (ProtectedSecret) input.readObject();
        }

        assertThat(secret1.getSecret(), is(secret2.getSecret()));
    }

}
