package example.server.function;

import org.apache.geode.cache.Cache;

import org.apache.geode.cache.wan.GatewaySender;
import org.apache.geode.internal.cache.RegionEntry;
import org.apache.geode.internal.size.ObjectGraphSizer;

import java.text.Format;
import java.text.NumberFormat;

public abstract class AbstractGatewaySenderQueueEntrySizer implements GatewaySenderQueueEntrySizer {

  protected Cache cache;
  
  protected GatewaySender sender;

  protected static final Format format = NumberFormat.getInstance();

  public AbstractGatewaySenderQueueEntrySizer(Cache cache, GatewaySender sender) {
    this.cache = cache;
    this.sender = sender;
  }

  protected long addAndReturnSize(StringBuilder builder, RegionEntry regionEntry, boolean summaryOnly) {
    long numBytes = 0l;
    ObjectGraphSizer.ObjectFilter filter = new GatewayQueueEventRegionEntryObjectFilter(this.cache);
    try {
      numBytes = ObjectGraphSizer.size(regionEntry, filter, false);
      if (!summaryOnly) {
        addEntry(builder, regionEntry, numBytes);
      }
    } catch (Exception e) {
      this.cache.getLogger().warning("Caught exception attempting to dump the size of " + regionEntry + ":", e);
    }
    return numBytes;
  }

  protected void addEntry(StringBuilder builder, RegionEntry regionEntry, long numBytes) {
    builder
      .append("\n\t\tkey=").append(regionEntry.getKey())
      .append("; entryClass=").append(regionEntry.getClass().getSimpleName())
      .append("; valueClass=").append(regionEntry.getValue().getClass().getSimpleName())
      .append("; numBytes=").append(format.format(numBytes));
  }
}
