package co.uk.mommyheather.futuregenerators.util;

import net.minecraftforge.energy.EnergyStorage;

public class FutureGeneratorsEnergyStorage extends EnergyStorage{

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


    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setEnergy(int i) {
        this.energy = i;
    }

    
}
