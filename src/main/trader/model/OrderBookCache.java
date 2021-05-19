package model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;

public class OrderBookCache {
    private static final String BIDS  = "BIDS";
    private static final String ASKS  = "ASKS";
    private Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;

    public OrderBookCache(Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache) {
        this.depthCache = depthCache;
    }

    public NavigableMap<BigDecimal, BigDecimal> getAsks() {
        return depthCache.get(ASKS);
    }

    public NavigableMap<BigDecimal, BigDecimal> getBids() {
        return depthCache.get(BIDS);
    }

    /**
     * @return the best ask in the order book
     */
    public Map.Entry<BigDecimal, BigDecimal> getBestAsk() {
        return getAsks().lastEntry();
    }

    /**
     * @return the best bid in the order book
     */
    public Map.Entry<BigDecimal, BigDecimal> getBestBid() {
        return getBids().firstEntry();
    }

    /**
     * @return a depth cache, containing two keys (ASKs and BIDs), and for each, an ordered list of book entries.
     */
    public Map<String, NavigableMap<BigDecimal, BigDecimal>> getDepthCache() {
        return this.depthCache;
    }
}
