package org.antarcticgardens.cna.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;

/**
 * Port of ESL's {@code SimpleEnergyStorage} onto Team Reborn Energy, keeping its fluent setters,
 * which CNA's block entities configure their storages with. Team Reborn's own
 * {@link team.reborn.energy.api.base.SimpleEnergyStorage} fixes capacity and limits at construction,
 * while the carbon brushes resize their buffer every tick.
 */
public class SimpleEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
    private long capacity;
    private long stored = 0;
    private boolean supportsInsertion = true;
    private boolean supportsExtraction = true;
    private long maxExtract = Long.MAX_VALUE;
    private long maxInsert = Long.MAX_VALUE;

    private Runnable finalCommitCallback = () -> {};

    public SimpleEnergyStorage(long capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("SimpleEnergyStorage capacity can't be negative");
        this.capacity = capacity;
    }

    public SimpleEnergyStorage onFinalCommit(Runnable callback) {
        finalCommitCallback = callback;
        return this;
    }

    public SimpleEnergyStorage setCapacity(long capacity) {
        this.capacity = capacity;
        stored = Math.min(stored, capacity);
        return this;
    }

    public SimpleEnergyStorage setSupportsInsertion(boolean supportsInsertion) {
        this.supportsInsertion = supportsInsertion;
        return this;
    }

    public SimpleEnergyStorage setSupportsExtraction(boolean supportsExtraction) {
        this.supportsExtraction = supportsExtraction;
        return this;
    }

    public SimpleEnergyStorage setMaxExtract(long maxExtract) {
        this.maxExtract = maxExtract;
        return this;
    }

    public SimpleEnergyStorage setMaxInsert(long maxInsert) {
        this.maxInsert = maxInsert;
        return this;
    }

    public SimpleEnergyStorage setStoredEnergy(long amount) {
        stored = Math.max(0, Math.min(amount, capacity));
        return this;
    }

    /** ESL's name for {@link #getAmount()}, kept so call sites read as upstream. */
    public long getStoredEnergy() {
        return stored;
    }

    @Override
    public long getAmount() {
        return stored;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public boolean supportsInsertion() {
        return supportsInsertion;
    }

    @Override
    public boolean supportsExtraction() {
        return supportsExtraction;
    }

    @Override
    public long insert(long amount, TransactionContext transaction) {
        if (!supportsInsertion)
            return 0;

        long inserted = Math.min(Math.min(amount, maxInsert), capacity - stored);
        if (inserted > 0) {
            updateSnapshots(transaction);
            stored += inserted;
        }
        return inserted;
    }

    @Override
    public long extract(long amount, TransactionContext transaction) {
        if (!supportsExtraction)
            return 0;

        long extracted = Math.min(Math.min(amount, maxExtract), stored);
        if (extracted > 0) {
            updateSnapshots(transaction);
            stored -= extracted;
        }
        return extracted;
    }

    /** Inserts outside a transaction and ignoring the insertion limits, for the owner's own use. */
    public long internalInsert(long amount, boolean simulated) {
        long inserted = Math.min(amount, capacity - stored);
        if (!simulated)
            stored += inserted;
        return inserted;
    }

    /** Extracts outside a transaction and ignoring the extraction limits, for the owner's own use. */
    public long internalExtract(long amount, boolean simulated) {
        long extracted = Math.min(amount, stored);
        if (!simulated)
            stored -= extracted;
        return extracted;
    }

    @Override
    protected Long createSnapshot() {
        return stored;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        stored = snapshot;
    }

    @Override
    protected void onFinalCommit() {
        finalCommitCallback.run();
    }
}
