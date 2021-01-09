package example.client.service;

import example.client.domain.CusipHelper;
import example.client.domain.Trade;
import example.client.function.AllServersFunctions;
import example.client.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

@Service
public class TradeService {

  @Autowired
  protected TradeRepository repository;

  @Autowired
  protected AllServersFunctions allServersFunctions;

  protected static final Random random = new Random();

  protected static final Logger logger = LoggerFactory.getLogger(TradeService.class);

  public void put(int numEntries, int entrySize) {
    logger.info("Putting {} trades of random size up to {} bytes", numEntries, entrySize);
    for (int i=0; i<numEntries; i++) {
      long createTime = System.currentTimeMillis();
      Trade trade = new Trade(String.valueOf(i), CusipHelper.getCusip(), random.nextInt(100), new BigDecimal(BigInteger.valueOf(random.nextInt(100000)), 2), new byte[random.nextInt(entrySize)], createTime, createTime);
      trade = this.repository.save(trade);
      logger.info("Saved " + trade);
    }
  }

  public Object calculateGatewaySenderQueueEntrySizes(String gatewaySenderId, boolean summaryOnly, boolean groupByBucket) {
    Object result = this.allServersFunctions.calculateGatewaySenderQueueEntrySizes(gatewaySenderId, summaryOnly, groupByBucket);
    logger.info("Calculated entry sizes for gatewaySenderId={}; summaryOnly={}; groupByBucket={}; result={}", gatewaySenderId, summaryOnly, groupByBucket, result);
    return result;
  }
}
