package model;

import com.binance.api.client.domain.market.AggTrade;

import java.util.Map;

public class AggsTradeCache {
    private Map<Long, AggTrade> aggTradesCache;

    public AggsTradeCache(Map<Long, AggTrade> aggTradesCache) {
        this.aggTradesCache = aggTradesCache;
    }

    /**
     * @return an aggTrades cache, containing the aggregated trade id as the key,
     * and the agg trade data as the value.
     */
    public Map<Long, AggTrade> getAggTradesCache() {
        return aggTradesCache;
    }
}
