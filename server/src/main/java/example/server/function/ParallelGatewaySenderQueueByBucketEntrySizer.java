package example.server.function;

import org.apache.geode.cache.Cache;

import org.apache.geode.cache.wan.GatewaySender;

import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.NonTXEntry;
import org.apache.geode.internal.cache.PartitionedRegion;

import org.apache.geode.internal.cache.wan.parallel.ParallelGatewaySenderQueue;

import java.util.Comparator;
import java.util.Set;

public class ParallelGatewaySenderQueueByBucketEntrySizer extends AbstractParallelGatewaySenderQueueEntrySizer {

  public ParallelGatewaySenderQueueByBucketEntrySizer(Cache cache, GatewaySender sender) {
    super(cache, sender);
  }
  
  public void calculateEntrySizes(boolean summaryOnly) {
    // Get the region implementing the queue
    PartitionedRegion region = (PartitionedRegion) this.cache.getRegion(sender.getId() + ParallelGatewaySenderQueue.QSTRING);
    StringBuilder builder = new StringBuilder();

    // Calculate the sizes of the primary queue entries
    Set<BucketRegion> primaryBrs = region.getDataStore().getAllLocalPrimaryBucketRegions();
    int primaryEntries = getNumEntries(primaryBrs);
    long primaryBytes = addAndReturnSizes(builder, primaryBrs, primaryEntries, summaryOnly, true);

    // Calculate the sizes of the secondary queue entries
    Set<BucketRegion> secondaryBrs = getLocalSecondaryBucketRegions(region);
    int secondaryEntries = getNumEntries(secondaryBrs);
    long secondaryBytes = addAndReturnSizes(builder, secondaryBrs, secondaryEntries, summaryOnly, false);

    // Add the summary
    addSummary(builder, primaryEntries, primaryBytes, secondaryEntries, secondaryBytes);

    // Log the results
    this.cache.getLogger().info(builder.toString());
  }

  private int getNumEntries(Set<BucketRegion> brs) {
    return brs
      .stream()
      .mapToInt(br -> br.size())
      .sum();
  }

  private long addAndReturnSizes(StringBuilder builder, Set<BucketRegion> brs, int numEntries, boolean summaryOnly, boolean isPrimary) {
  if (!summaryOnly) {
    addHeader(builder, brs.size(), numEntries, isPrimary);
  }

  return brs
      .stream()
      .sorted(Comparator.comparingInt(BucketRegion::getId))
      .mapToLong(br -> addAndReturnSizes(builder, br, summaryOnly))
      .sum();
  }

  private void addHeader(StringBuilder builder, int numBuckets, int numEntries, boolean isPrimary) {
    builder
      .append("\nParallel GatewaySender ")
      .append(this.sender.getId())
      .append(" contains the following ")
      .append(numBuckets)
      .append(isPrimary ? " primary" : " secondary")
      .append(" buckets consisting of ")
      .append(numEntries)
      .append(" total entries and sizes grouped by bucket:\n");
  }

  private long addAndReturnSizes(StringBuilder builder, BucketRegion br, boolean summaryOnly) {
    if (!summaryOnly) {
      addHeader(builder, br);
    }

    long totalBytes = ((Set<NonTXEntry>) br.entrySet())
      .stream()
      .map(entry -> entry.getRegionEntry())
      .sorted((entry1, entry2) -> Long.compare((long) entry1.getKey(), (long) entry2.getKey()))
      .mapToLong(entry -> addAndReturnSize(builder, entry, summaryOnly))
      .sum();

    if (!summaryOnly) {
      builder
        .append("\n\n\tBucket ")
        .append(br.getId())
        .append(" contains ")
        .append(format.format(totalBytes))
        .append(" total bytes")
        .append("\n");
    }
    return totalBytes;
  }

  private void addHeader(StringBuilder builder, BucketRegion br) {
    builder
      .append("\n\tBucket ")
      .append(br.getId())
      .append(" contains the following ")
      .append(br.size())
      .append(" entries and sizes:\n");
  }
}
