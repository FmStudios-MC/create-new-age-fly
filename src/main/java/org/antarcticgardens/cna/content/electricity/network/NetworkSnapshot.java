package org.antarcticgardens.cna.content.electricity.network;

/**
 * The network's own state inside a transaction: how much of each path's conductivity is used up.
 * <p>
 * Under ESL this also captured and restored every connected storage. Fabric's transactions already
 * do that: {@link ElectricalNetwork#insert} passes the transaction on to each consumer, which
 * snapshots and rolls back itself, and the snapshot methods of other mods' storages are not
 * accessible from here anyway.
 */
public class NetworkSnapshot {
    private final NetworkPathConductivityContext context;

    public NetworkSnapshot(ElectricalNetwork network) {
        context = new NetworkPathConductivityContext(network.getPathManager().getConductivityContext());
    }

    public NetworkPathConductivityContext getContext() {
        return context;
    }
}
