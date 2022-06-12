package cn.zjavax;

import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.examples.BaseTest;
import com.bloxbean.cardano.client.exception.AddressExcepion;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.function.Output;
import com.bloxbean.cardano.client.function.TxBuilder;
import com.bloxbean.cardano.client.function.TxBuilderContext;
import com.bloxbean.cardano.client.transaction.spec.Transaction;

import static com.bloxbean.cardano.client.common.ADAConversionUtil.adaToLovelace;
import static com.bloxbean.cardano.client.common.CardanoConstants.LOVELACE;
import static com.bloxbean.cardano.client.function.helper.ChangeOutputAdjustments.adjustChangeOutput;
import static com.bloxbean.cardano.client.function.helper.FeeCalculators.feeCalculator;
import static com.bloxbean.cardano.client.function.helper.InputBuilders.createFromSender;

//Build with Java backend + Sign with Nami/cc frontend
public class BuildWithJavaBackend extends BaseTest {
    public static void main(String[] args) throws AddressExcepion, CborSerializationException, ApiException {
        new BuildWithJavaBackend().transfer();
        System.exit(1);
    }

    public void transfer() throws CborSerializationException, ApiException, AddressExcepion {
//        String senderMnemonic = "kit color frog trick speak employ suit sort bomb goddess jewel primary spoil fade person useless measure manage warfare reduce few scrub beyond era";
        String senderAddress = "addr_test1qqxnp3khzm7kcj9t23hskehat7428ghsenk0pfew4rqy5v9frnmht7uwrl073q4jvq20z82kh4rksyns540azhndqexqpvhgqr";
        String receiverAddress1 = "addr_test1qqwpl7h3g84mhr36wpetk904p7fchx2vst0z696lxk8ujsjyruqwmlsm344gfux3nsj6njyzj3ppvrqtt36cp9xyydzqzumz82";

        Output output1 = Output.builder()
                .address(receiverAddress1)
                .assetName(LOVELACE)
                .qty(adaToLovelace(2.1))
                .build();

        TxBuilder txBuilder = (output1.outputBuilder()
                .buildInputs(createFromSender(senderAddress, senderAddress))
        )
                .andThen(feeCalculator(senderAddress, 1))
                .andThen(adjustChangeOutput(senderAddress, 1));

        Transaction signedTransaction = TxBuilderContext.init(utxoSupplier, protocolParamsSupplier).build(txBuilder);

        System.out.println("txHexDraft=\"" +signedTransaction.serializeToHex()+"\"");

    }
}
