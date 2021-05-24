package logic.calc;

import static java.lang.Math.exp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import model.OrderBookCache;

public class Math {
    public static double calculateWeightedPrice(OrderBookCache orderBookCache) {
        double bestAskPrice
                = orderBookCache.getBestAsk().getKey().doubleValue();
        double bestAskQuantity
                = orderBookCache.getBestAsk().getValue().doubleValue();
        double bestBidPrice
                = orderBookCache.getBestBid().getKey().doubleValue();
        double bestBidQuantity
                = orderBookCache.getBestBid().getValue().doubleValue();
        double totalQuantity
                = bestAskQuantity + bestBidQuantity;
        double weightedAskPrice = (bestAskPrice * bestAskQuantity)/totalQuantity;
        double weightedBidPrice = (bestBidPrice * bestBidQuantity)/totalQuantity;
        return weightedAskPrice + weightedBidPrice;
    }

    public static Double getOrderImbalance(int depth, OrderBookCache orderBookCache) {
        List<BigDecimal> asks = new ArrayList<>(orderBookCache.getAsks().values());
        List<BigDecimal> bids = new ArrayList<>(orderBookCache.getBids().values());
        Double weightedVolumeBid = 0.0;
        Double weightedVolumeAsk = 0.0;
        for (int i = 0; i < depth; i++) {
            weightedVolumeAsk += (exp(-0.5 * i) * asks.get(i).doubleValue());
            weightedVolumeBid += (exp(-0.5 * i) * bids.get(i).doubleValue());
        }
        Double imbalance = (weightedVolumeBid - weightedVolumeAsk) / (weightedVolumeAsk + weightedVolumeBid);
        return imbalance;
    }
}
