/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nodyn.crypto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Hmac {

    private final HMac hmac;

    public Hmac(Digest digest, ByteBuf key) throws InvalidKeyException {
        this.hmac = new HMac( digest );
        computeKey(key);
    }

    private void computeKey(ByteBuf key) throws InvalidKeyException {
        byte[] keyBytes = new byte[ key.readableBytes() ];
        key.readBytes( keyBytes );
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, this.hmac.getUnderlyingDigest().getAlgorithmName() );
        KeyParameter param = new KeyParameter(secretKey.getEncoded());
        this.hmac.init(param);
    }

    public void update(ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), bytes);
        this.hmac.update(bytes, 0, bytes.length);
    }

    public ByteBuf digest() throws NoSuchAlgorithmException {
        byte[] digestBytes = new byte[this.hmac.getMacSize()];
        this.hmac.doFinal(digestBytes, 0);
        return Unpooled.wrappedBuffer(digestBytes);
    }


}
