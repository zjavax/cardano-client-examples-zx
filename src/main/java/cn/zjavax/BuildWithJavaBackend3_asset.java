package cn.zjavax;

import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.backend.model.Asset;
import com.bloxbean.cardano.client.common.ADAConversionUtil;
import com.bloxbean.cardano.client.examples.BaseTest;
import com.bloxbean.cardano.client.exception.AddressExcepion;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.function.Output;
import com.bloxbean.cardano.client.function.TxBuilder;
import com.bloxbean.cardano.client.function.TxBuilderContext;
import com.bloxbean.cardano.client.function.TxOutputBuilder;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.bloxbean.cardano.client.util.HexUtil;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.bloxbean.cardano.client.common.ADAConversionUtil.adaToLovelace;
import static com.bloxbean.cardano.client.common.CardanoConstants.LOVELACE;
import static com.bloxbean.cardano.client.function.helper.ChangeOutputAdjustments.adjustChangeOutput;
import static com.bloxbean.cardano.client.function.helper.FeeCalculators.feeCalculator;
import static com.bloxbean.cardano.client.function.helper.InputBuilders.createFromSender;

//Build with Java backend + Sign with Nami/cc frontend
public class BuildWithJavaBackend3_asset extends BaseTest {
    public static void main(String[] args) throws AddressExcepion, CborSerializationException, ApiException {
//        String senderMnemonic = "kit color frog trick speak employ suit sort bomb goddess jewel primary spoil fade person useless measure manage warfare reduce few scrub beyond era";
        String senderAddress = "addr_test1qqxnp3khzm7kcj9t23hskehat7428ghsenk0pfew4rqy5v9frnmht7uwrl073q4jvq20z82kh4rksyns540azhndqexqpvhgqr";

        TransferAsset transferAsset = new TransferAsset();
        transferAsset.setPolicyId("769c4c6e9bc3ba5406b9b89fb7beb6819e638ff2e2de63f008d5bcff");
        transferAsset.setAssetName("tNEWM");
        transferAsset.setAmount(10.000001);
        transferAsset.setReceiverAddress("addr_test1qq4pxvsevncnfd7ppmatyavej7kjcukwxugrnk9rsejufvpfqqgflwz7ahhqezd8mx5hfxmwh2stfagm8uwkxreya6rsqfxnz7");

        TransferAsset transferAsset1 = new TransferAsset();
        transferAsset1.setPolicyId("75d01e750b6e4986f4d26f92d70c70a9d337e834faebcc177a7e796a");
        transferAsset1.setAssetName("TestCoin");
        transferAsset1.setAmount(10);
        transferAsset1.setReceiverAddress("addr_test1qq4pxvsevncnfd7ppmatyavej7kjcukwxugrnk9rsejufvpfqqgflwz7ahhqezd8mx5hfxmwh2stfagm8uwkxreya6rsqfxnz7");
        TransferAsset transferAsset2 = new TransferAsset();
        transferAsset2.setPolicyId("75d01e750b6e4986f4d26f92d70c70a9d337e834faebcc177a7e796a");
        transferAsset2.setAssetName("TestCoin");
        transferAsset2.setAmount(10);
        transferAsset2.setReceiverAddress("addr_test1qqqvjp4ffcdqg3fmx0k8rwamnn06wp8e575zcv8d0m3tjn2mmexsnkxp7az774522ce4h3qs4tjp9rxjjm46qf339d9sk33rqn");

        List<TransferAsset> transferAssetList = List.of(transferAsset, transferAsset1,transferAsset2);

        new BuildWithJavaBackend3_asset().transfer(senderAddress, transferAssetList);
        System.exit(1);
    }

    public void transfer(String senderAddress, List<TransferAsset> transferAssetList) throws CborSerializationException, ApiException, AddressExcepion {
        TxOutputBuilder txOutputBuilder=null;
        for (TransferAsset transferAsset : transferAssetList) {
            Long decimals = transferAsset.getDecimals();
            if(decimals == null){
                decimals = new BuildWithJavaBackend3_asset().getTokenDecimals(transferAsset.getPolicyId(), transferAsset.getAssetName());
            }
            Output output = Output.builder()
                    .address(transferAsset.getReceiverAddress())
                    .policyId(transferAsset.getPolicyId())
                    .assetName(transferAsset.getAssetName())
                    .qty(ADAConversionUtil.assetFromDecimal(BigDecimal.valueOf(transferAsset.getAmount()), decimals))
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

    public long getTokenDecimals(String policyId, String assetName) throws ApiException {
        String assetNameHexString = HexUtil.encodeHexString(assetName.getBytes(StandardCharsets.UTF_8));
        Result<Asset> asset = assetService.getAsset(policyId + assetNameHexString);
        JsonNode decimals = asset.getValue().getMetadata().get("decimals");
        return decimals.asLong();
    }
}

class TransferAsset {
    String receiverAddress;
    String policyId; // ada的policyId为null
    String assetName; // ada: "lovelace"
    double amount;
    Long decimals;  // 前台可以传，也可以不传

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Long getDecimals() {
        return decimals;
    }

    public void setDecimals(Long decimals) {
        this.decimals = decimals;
    }
}
