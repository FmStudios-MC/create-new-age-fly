package org.antarcticgardens.cna.content.electricity.network;


import org.antarcticgardens.cna.content.electricity.connector.AbstractElectricalConnector;
import team.reborn.energy.api.EnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;


public class NetworkEnergyStorage extends SnapshotParticipant<Object> implements EnergyStorage {
    private final AbstractElectricalConnector connector;
    private ElectricalNetwork network;

    public NetworkEnergyStorage(AbstractElectricalConnector connector, ElectricalNetwork network) {
        this.connector = connector;
        this.network = network;
    }
    
    public ElectricalNetwork getNetwork() {
        return network;
    }
    
    public void setNetwork(ElectricalNetwork network) {
        this.network = network;
    }

    @Override
    public long insert(long maxAmount, TransactionContext txn) {
        if (network == null)
            return 0;

        updateSnapshots(txn);
        
        return network.insert(connector, maxAmount, txn);
    }

    @Override
    public long extract(long maxAmount, TransactionContext txn) {
        return 0;
    }

    @Override
    public long getAmount() {
        return 0;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }

    @Override
    protected Object createSnapshot() {
        if (network == null)
            return null;

        return new NetworkSnapshot(network);
    }

    @Override
    protected void readSnapshot(Object object) {
        if (object instanceof NetworkSnapshot snapshot)
            getNetwork().getPathManager().setConductivityContext(new NetworkPathConductivityContext(snapshot.getContext()));
    }
}
