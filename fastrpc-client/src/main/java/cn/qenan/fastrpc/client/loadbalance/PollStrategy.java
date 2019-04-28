package cn.qenan.fastrpc.client.loadbalance;

import javafx.util.Pair;

public class PollStrategy extends AbstractLoadBalance {
    @Override
    String getAddressByLoadBlance(Pair<String, String> pair) {
        return null;
    }
}
