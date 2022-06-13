package cn.zjavax;

import com.bloxbean.cardano.client.common.ADAConversionUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ADAConversionUtilTest {

    public static void main(String[] args) {
        BigInteger bigInteger = ADAConversionUtil.assetFromDecimal(BigDecimal.valueOf(10), 0L); // 后面加几个0
    }
}
