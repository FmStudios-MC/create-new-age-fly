package org.antarcticgardens.cna.content.electricity.network;

import org.antarcticgardens.cna.content.electricity.connector.AbstractElectricalConnector;
import org.antarcticgardens.esl.energy.EnergyStorage;
import org.antarcticgardens.esl.transaction.SnapshotParticipant;

import java.util.HashMap;
import java.util.Map;

public class NetworkSnapshot {
    private final Map<AbstractElectricalConnector, Object> snapshots = new HashMap<>();
    private final NetworkPathConductivityContext context;
    
    public NetworkSnapshot(ElectricalNetwork network) {
        context = new NetworkPathConductivityContext(network.getPathManager().getConductivityContext());
        
        for (AbstractElectricalConnector connector : network.getNodes()) {
            EnergyStorage storage = EnergyStorage.findForBlock(connector.getLevel(), connector.getSupportingBlockPos(), 
                    connector.getFacing());
            
            if (storage instanceof SnapshotParticipant<?> snapshotParticipant && !(storage instanceof NetworkEnergyStorage)) {
                snapshots.put(connector, snapshotParticipant.createSnapshot());
            }
        }
    }
    
    public Map<AbstractElectricalConnector, Object> getSnapshots() {
        return snapshots;
    }
    
    public NetworkPathConductivityContext getContext() {
        return context;
    }
}
