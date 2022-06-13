package com.bloxbean.cardano.client.examples;

import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.backend.model.Asset;
import com.bloxbean.cardano.client.util.HexUtil;
import com.bloxbean.cardano.client.util.JsonUtil;

import java.nio.charset.StandardCharsets;

public class AssetTest_nft extends BaseTest {

    public AssetTest_nft() {
        super();
    }

    public void getAssets() throws ApiException {
        String policyId = "eadb83a1b25dcc8d23e289a4c8a24198f987886f284035bf4078d3c3";
        String assetName = HexUtil.encodeHexString("testNFT".getBytes(StandardCharsets.UTF_8));

        Result<Asset> asset = assetService.getAsset(policyId + assetName);

        System.out.println(JsonUtil.getPrettyJson(asset.getValue()));
    }

    public static void main(String[] args) throws ApiException {
        AssetTest_nft assetTest = new AssetTest_nft();
        assetTest.getAssets();
        System.exit(1);
    }
}
