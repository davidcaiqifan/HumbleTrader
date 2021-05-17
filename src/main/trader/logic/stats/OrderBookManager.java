package logic.stats;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import logic.EventManager;
import logic.eventproducers.EventProducer;
import model.Event;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class OrderBookManager implements StatsManager {
//    private static final String BIDS  = "BIDS";
//    private static final String ASKS  = "ASKS";
//    private long lastUpdateId;
//    private Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;
//    private EventManager eventManager;
//    private LinkedBlockingDeque<Event> depthEventQueue;
//
//    public OrderBookManager(EventManager eventManager) {
//        this.eventManager = eventManager;
//        initializeDepthCache("BTCUSDT");
//        int index = eventManager.addEventStream("orderbook");
//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                startDepthEventStreaming(index);
//            }
//        });
//        t1.start();
//    }
//
//    /**
//     * Initializes the depth cache by using the REST API.
//     */
//    private void initializeDepthCache(String symbol) {
//        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
//        BinanceApiRestClient client = factory.newRestClient();
//        OrderBook orderBook = client.getOrderBook(symbol.toUpperCase(), 10);
//
//        this.depthCache = new ConcurrentHashMap<>();
//        this.lastUpdateId = orderBook.getLastUpdateId();
//
//        NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(Comparator.reverseOrder());
//        for (OrderBookEntry ask : orderBook.getAsks()) {
//            asks.put(new BigDecimal(ask.getPrice()), new BigDecimal(ask.getQty()));
//        }
//        depthCache.put(ASKS, asks);
//
//        NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Comparator.reverseOrder());
//        for (OrderBookEntry bid : orderBook.getBids()) {
//            bids.put(new BigDecimal(bid.getPrice()), new BigDecimal(bid.getQty()));
//        }
//        depthCache.put(BIDS, bids);
//    }
//
//    /**
//     * Begins streaming of depth events.
//     */
//    private void startDepthEventStreaming(int eventQueueIndex) {
//        this.depthEventQueue = eventManager.getEventQueue(eventQueueIndex);
//        while(true) {
//            try {
//                DepthEvent newEvent = this.depthEventQueue.take().getDepthEvent();
//                lastUpdateId = newEvent.getFinalUpdateId();
//                updateOrderBook(getAsks(), newEvent.getAsks());
//                updateOrderBook(getBids(), newEvent.getBids());
//                //printDepthCache();
//            } catch(InterruptedException e) {
//                System.out.println(e);
//            }
//        }
//    }
//
//    /**
//     * Updates an order book (bids or asks) with a delta received from the server.
//     *
//     * Whenever the qty specified is ZERO, it means the price should was removed from the order book.
//     */
//    private void updateOrderBook(NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries, List<OrderBookEntry> orderBookDeltas) {
//        for (OrderBookEntry orderBookDelta : orderBookDeltas) {
//            BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
//            BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
//            if (qty.compareTo(BigDecimal.ZERO) == 0) {
//                // qty=0 means remove this level
//                lastOrderBookEntries.remove(price);
//            } else {
//                lastOrderBookEntries.put(price, qty);
//            }
//        }
//    }
//
//    public NavigableMap<BigDecimal, BigDecimal> getAsks() {
//        return depthCache.get(ASKS);
//    }
//
//    public NavigableMap<BigDecimal, BigDecimal> getBids() {
//        return depthCache.get(BIDS);
//    }
//
//    /**
//     * @return the best ask in the order book
//     */
//    public Map.Entry<BigDecimal, BigDecimal> getBestAsk() {
//        return getAsks().lastEntry();
//    }
//
//    /**
//     * @return the best bid in the order book
//     */
//    public Map.Entry<BigDecimal, BigDecimal> getBestBid() {
//        return getBids().firstEntry();
//    }
//
//    /**
//     * @return a depth cache, containing two keys (ASKs and BIDs), and for each, an ordered list of book entries.
//     */
//    public Map<String, NavigableMap<BigDecimal, BigDecimal>> getDepthCache() {
//        return depthCache;
//    }
//
//    /**
//     * Prints the cached order book / depth of a symbol as well as the best ask and bid price in the book.
//     */
//    private void printDepthCache() {
////        System.out.println(depthCache);
////        System.out.println("ASKS:");
////        getAsks().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
////        System.out.println("BIDS:");
////        getBids().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
//        System.out.println("BEST ASK: " + toDepthCacheEntryString(getBestAsk()));
//        System.out.println("BEST BID: " + toDepthCacheEntryString(getBestBid()));
//    }
//
//    /**
//     * Pretty prints an order book entry in the format "price / quantity".
//     */
//    public static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
//        return depthCacheEntry.getKey().toPlainString() + " / " + depthCacheEntry.getValue();
//    }
//
//    public static void main(String[] args) {
//        new OrderBookManager(new EventManager());
//    }
}
