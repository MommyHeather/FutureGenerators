package co.uk.mommyheather.futuregenerators.util;

import net.minecraftforge.energy.EnergyStorage;

public abstract class FutureGeneratorsEnergyStorage extends EnergyStorage{

    public FutureGeneratorsEnergyStorage(int capacity) {
        super(capacity, Integer.MAX_VALUE);
    }

    public FutureGeneratorsEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public FutureGeneratorsEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public FutureGeneratorsEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        onContentsChanged();
        return super.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        onContentsChanged();
        return super.extractEnergy(maxExtract, simulate);
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setEnergy(int i) {
        this.energy = i;
    }

    public abstract void onContentsChanged();

    
}
