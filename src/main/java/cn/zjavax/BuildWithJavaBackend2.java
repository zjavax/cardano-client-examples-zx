package cn.zjavax;

import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.examples.BaseTest;
import com.bloxbean.cardano.client.exception.AddressExcepion;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.function.Output;
import com.bloxbean.cardano.client.function.TxBuilder;
import com.bloxbean.cardano.client.function.TxBuilderContext;
import com.bloxbean.cardano.client.function.TxOutputBuilder;
import com.bloxbean.cardano.client.transaction.spec.Transaction;

import java.util.List;

import static com.bloxbean.cardano.client.common.ADAConversionUtil.adaToLovelace;
import static com.bloxbean.cardano.client.common.CardanoConstants.LOVELACE;
import static com.bloxbean.cardano.client.function.helper.ChangeOutputAdjustments.adjustChangeOutput;
import static com.bloxbean.cardano.client.function.helper.FeeCalculators.feeCalculator;
import static com.bloxbean.cardano.client.function.helper.InputBuilders.createFromSender;

//Build with Java backend + Sign with Nami/cc frontend
public class BuildWithJavaBackend2 extends BaseTest {
    public static void main(String[] args) throws AddressExcepion, CborSerializationException, ApiException {
//        String senderMnemonic = "kit color frog trick speak employ suit sort bomb goddess jewel primary spoil fade person useless measure manage warfare reduce few scrub beyond era";
        String senderAddress = "addr_test1qqxnp3khzm7kcj9t23hskehat7428ghsenk0pfew4rqy5v9frnmht7uwrl073q4jvq20z82kh4rksyns540azhndqexqpvhgqr";
        String receiverAddress = "addr_test1qqwpl7h3g84mhr36wpetk904p7fchx2vst0z696lxk8ujsjyruqwmlsm344gfux3nsj6njyzj3ppvrqtt36cp9xyydzqzumz82";
        String receiverAddress2 = "addr_test1qp6vl670t6t5wntm38yx94lw0nlj9mamqvk5zvaphpxtmnfa0zp6xfaqxlxwqve2tkvq5q6xmgrmuxy8fd0c92cm80pqpjtpwq";

        List<String> receiverAddressList = List.of(receiverAddress,receiverAddress2);

        new BuildWithJavaBackend2().transfer(senderAddress, receiverAddressList, 2.1);
        System.exit(1);
    }

    public void transfer(String senderAddress, List<String> receiverAddressList, double amount) throws CborSerializationException, ApiException, AddressExcepion {
        TxOutputBuilder txOutputBuilder=null;
        for (String receiverAddress : receiverAddressList) {
            Output output = Output.builder()
                    .address(receiverAddress)
                    .assetName(LOVELACE)
                    .qty(adaToLovelace(amount))
                    .build();
            if(txOutputBuilder==null) {
                txOutputBuilder = output.outputBuilder();
            } else {
                txOutputBuilder.and(output.outputBuilder());
            }
        }
        TxBuilder txBuilder = (txOutputBuilder.buildInputs(createFromSender(senderAddress, senderAddress)))
                .andThen(feeCalculator(senderAddress, 1))
                .andThen(adjustChangeOutput(senderAddress, 1));

        Transaction signedTransaction = TxBuilderContext.init(utxoSupplier, protocolParamsSupplier).build(txBuilder);

        System.out.println("txHexDraft=\"" +signedTransaction.serializeToHex()+"\"");

    }
}
