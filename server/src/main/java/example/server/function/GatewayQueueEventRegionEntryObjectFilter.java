package example.server.function;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.TransactionId;

import org.apache.geode.internal.cache.EnumListenerEvent;
import org.apache.geode.internal.cache.EventID;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.RegionEntry;

import org.apache.geode.internal.cache.wan.GatewaySenderEventImpl;

import org.apache.geode.internal.size.ObjectGraphSizer.ObjectFilter;

public class GatewayQueueEventRegionEntryObjectFilter implements ObjectFilter {
    
	private boolean logAllClasses = false;
	
	private boolean logRejectedClasses = false;
	
	private boolean logAcceptedClasses = false;

	private final Cache cache;

	public GatewayQueueEventRegionEntryObjectFilter(Cache cache) {
		this.cache = cache;
	}

	public boolean accept(Object parent, Object object) {
    // NOTE: Be careful about dumping the value here in the logging. That might cause it to be altered.
		boolean accept = true;
		String parentClassName = null;
		if (this.logAllClasses || this.logRejectedClasses || this.logAcceptedClasses) {
			if (parent != null) {
				parentClassName = parent.getClass().getName();
			}
		}
		// Reject if the object is a RegionEntry and its parent is non-null (e.g. VMThinDiskLRURegionEntryHeapLongKey.nextEntry)
		if (object instanceof RegionEntry && parent != null
		    || object instanceof EnumListenerEvent
		    || object instanceof PartitionedRegion
		    || object instanceof TransactionId
		    || object instanceof EventID
		    || (object instanceof String && parent instanceof GatewaySenderEventImpl)
		) {
			if (this.logAllClasses || this.logRejectedClasses) {
				this.cache.getLogger().info("Rejecting object=" + object + " objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + "); parent=" + parent + " parentIdentity=" + System.identityHashCode(parent) + " (an instance of " + parentClassName + ")");
			}
			accept = false;
		} else {
			if (this.logAllClasses || this.logAcceptedClasses) {
				this.cache.getLogger().info("Accepting object=" + object + " objectIdentity=" + System.identityHashCode(object) + " (an instance of " + object.getClass().getName() + "); parent=" + parent + " parentIdentity=" + System.identityHashCode(parent) + " (an instance of " + parentClassName + ")");
			}
		}
		return accept;
	}
}