package com.bloxbean.cardano.client.examples.function;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.examples.BaseTest;
import com.bloxbean.cardano.client.exception.AddressExcepion;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import io.blockfrost.sdk.api.exception.APIException;
import io.blockfrost.sdk.api.model.AccountAsset;

import java.util.List;

public class StakeAddressTest extends BaseTest {
    public static void main(String[] args) throws AddressExcepion, CborSerializationException, ApiException, APIException {
        new StakeAddressTest().transfer();
        System.exit(1);
    }

    public void transfer() throws CborSerializationException, ApiException, AddressExcepion, APIException {
        String senderMnemonic = "kit color frog trick speak employ suit sort bomb goddess jewel primary spoil fade person useless measure manage warfare reduce few scrub beyond era";
        Account sender = new Account(Networks.testnet(), senderMnemonic);
        String stakeAddress = sender.stakeAddress();
        System.out.println(stakeAddress);

        // https://docs.blockfrost.io/#tag/Cardano-Accounts/paths/~1accounts~1{stake_address}~1addresses~1assets/get
        List<AccountAsset> list = accountService.getAccountAssets(stakeAddress,100,1);

        System.out.println();




    }
}
