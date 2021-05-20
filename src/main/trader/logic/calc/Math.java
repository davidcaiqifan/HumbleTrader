package logic.calc;

import model.OrderBookCache;

public class Math {
    public static double calculateWeightedPrice(OrderBookCache orderBookCache) {
        double bestAskPrice = orderBookCache.getBestAsk().getKey().doubleValue();
        double bestAskQuantity = orderBookCache.getBestAsk().getValue().doubleValue();
        double bestBidPrice = orderBookCache.getBestBid().getKey().doubleValue();
        double bestBidQuantity = orderBookCache.getBestBid().getValue().doubleValue();;
        double totalQuantity = bestAskQuantity + bestBidQuantity;
        double weightedAskPrice = (bestAskPrice * bestAskQuantity)/totalQuantity;
        double weightedBidPrice = (bestBidPrice * bestBidQuantity)/totalQuantity;
        double weightedMidPrice = weightedAskPrice + weightedBidPrice;
        return weightedMidPrice;
    }
}
