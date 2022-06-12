package com.bloxbean.cardano.client.examples.function;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.common.model.Networks;
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
import static com.bloxbean.cardano.client.function.helper.SignerProviders.signerFrom;

// 当一个utxo的ada不够时，这个代码显示使用了两个utxo
// txid: 50c82dbe17a73095b21e3c39368518ea6b97126f25ee1c05f11d1aa4463265fc
public class TransferTransactionTest2 extends BaseTest {
    public static void main(String[] args) throws AddressExcepion, CborSerializationException, ApiException {
        new TransferTransactionTest2().transfer();
        System.exit(1);
    }

    public void transfer() throws CborSerializationException, ApiException, AddressExcepion {
        String senderMnemonic = "around submit turtle canvas friend remind push vehicle debate drop blouse piece obvious crane tone avoid aspect power milk eye brand cradle tide wrist";
        Account sender = new Account(Networks.testnet(), senderMnemonic);
        String senderAddress = sender.baseAddress();

        String receiverAddress = "addr_test1qrs2a2hjfs2wt8r3smzwmptezmave3yjgws068hp0qsflmcypglx0rl69tp49396282ns02caz4cx7a2n290h2df0j3qjku4dy";

        Output output = Output.builder()
                .address(receiverAddress)
                .assetName(LOVELACE)
                .qty(adaToLovelace(128))
                .build();

        TxBuilder txBuilder = output.outputBuilder()
                .buildInputs(createFromSender(senderAddress, senderAddress))
                .andThen(feeCalculator(senderAddress, 1))
                .andThen(adjustChangeOutput(senderAddress, 1));

        Transaction signedTransaction = TxBuilderContext.init(utxoSupplier, protocolParamsSupplier)
                .buildAndSign(txBuilder, signerFrom(sender));

        Result<String> result = transactionService.submitTransaction(signedTransaction.serialize());
        System.out.println(result);

        if (result.isSuccessful())
            System.out.println("Transaction Id: " + result.getValue());
        else
            System.out.println("Transaction failed: " + result);

        waitForTransactionHash(result);
    }
}
