package de.ice09.excel2blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static Web3j httpWeb3;
    private static Web3j wsckWeb3;

    private static Credentials credIdentity;
    private static ExcelStorage excelStorageContractHttp;
    private static ExcelStorage excelStorageContractWsck;

    public static void main(String[] args) throws Exception {
        connectToBlockchain();
        deployExcelContractToBlockchain();
        addEventListenerForContractEvents();
        addRowsToBlockchain();
    }

    private static void connectToBlockchain() throws IOException {
        httpWeb3 = Web3j.build(new HttpService("http://localhost:8545"));
        WebSocketService wsck = new WebSocketService("ws://localhost:8545", true);
        wsck.connect();
        wsckWeb3 = Web3j.build(wsck);
        log.info("Connected to HTTP/JSON Ethereum client version: " + httpWeb3.web3ClientVersion().send().getWeb3ClientVersion());
        log.info("Connected to Websocket Ethereum client version: " + wsckWeb3.web3ClientVersion().send().getWeb3ClientVersion());

        String pkDeployer = "c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3";
        credIdentity = Credentials.create(pkDeployer);
        BigInteger balance = httpWeb3.ethGetBalance(credIdentity.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
        log.info("Deployer address " + credIdentity.getAddress() + " has " + balance + " wei.");
    }

    private static void deployExcelContractToBlockchain() throws Exception {
        excelStorageContractHttp = ExcelStorage.deploy(httpWeb3, credIdentity, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT).send();
        log.info("Deployed contract at " + excelStorageContractHttp.getContractAddress());
        excelStorageContractWsck = ExcelStorage.load(excelStorageContractHttp.getContractAddress(), wsckWeb3, credIdentity, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private static void addEventListenerForContractEvents() {
        /*
         * NOTE: this must be different for Ganache and Geth/Infura, see https://github.com/web3j/web3j/issues/405
         */
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, excelStorageContractHttp.getContractAddress().substring(2));
        String encodedEventSignature = EventEncoder.encode(ExcelStorage.STORED_EVENT);
        filter.addSingleTopic(encodedEventSignature);
        log.info("Subscribe to event Stored with EthFilter");
        excelStorageContractWsck.storedEventObservable(filter).subscribe(event -> log.info("key | value : " + event._key + " | " + event._value));
    }

    private static void addRowsToBlockchain() throws Exception {
        ExcelReader excelReader = new ExcelReader();
        List<Map<String, String>> excelData = excelReader.readExcel();
        int index = 0;
        for (Map<String, String> rowData : excelData) {
            for (String key : rowData.keySet()) {
                excelStorageContractHttp.put(BigInteger.valueOf(index), key, rowData.get(key)).send();
            }
            index++;
        }
    }

}
