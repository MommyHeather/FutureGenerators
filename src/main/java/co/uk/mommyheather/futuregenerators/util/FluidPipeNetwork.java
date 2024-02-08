package co.uk.mommyheather.futuregenerators.util;

import java.util.ArrayList;
import java.util.HashMap;

import co.uk.mommyheather.futuregenerators.tile.TileFluidPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FluidPipeNetwork {
    
    //Maps coordinates to all 
    public HashMap<Long, TileFluidPipe> pipes = new HashMap<>();

    //Lets each pipe track if a network has changed, so it knows whether or not it needs to rebuild its ordered cache
    //Each pipe has a separate ordered cache that can have a different order, so we can't have a centralised cache
    private ArrayList<Long> changedPipes = new ArrayList<>();
    
    //We use the coordinates of the pipe that created the network as an identifier.
    //It's unique enough as no two unique networks will ever exist in the same spot.

    //Can't be final as we need to override it later.
    public long id;


    //Called by the fluid pipe that creates a network.
    public FluidPipeNetwork(long id) {
        this.id = id;
    }

    //Called by the fluid pipes on first tick if it has no network. The pipe will search for networks in its adjacent blocks, and if it finds only a singular network, it'll just add itself.
    //If it finds none, it'll make a new network then add itself.
    //If it finds multiple, it'll add itself to one, then call mergeIntoSelf on that one, providing the others as arguments.                                                                                                                                                                                                                                                                                                                                                                                                                                              .
    public void addPipe(long pos, TileFluidPipe pipe) {
        if (pipes.containsKey(pos)) {
            throw new RuntimeException(String.format("Pipe with position {} added to a network that already had a pipe in that position!", pos));
        }
        pipes.put(pos, pipe);
        setChanged();
    }
    
    //Called by a fluid pipe when it gets broken.
    //If the pipe is touching two or more other pipes, a rebuild is also required.
    //In those situations, the pipe called rebuildNetwork.
    public void removePipe(long pos) {        
        if (!pipes.containsKey(pos)) {
            throw new RuntimeException(String.format("Pipe with position {} attempted to remove from a network that had no pipe in that position!", pos));
        }
        pipes.remove(pos);

        setChanged();
        
    }

    //Merges the provided networks into this one. The others can then be discarded.
    //Called by a placed pipe if the pipe merges multiple networks.
    public void mergeIntoSelf(FluidPipeNetwork... networks) {
        for (FluidPipeNetwork network : networks) {
            for (TileFluidPipe pipe : network.pipes.values()) {
                pipe.network = this;
            }
            pipes.putAll(network.pipes);
            network.pipes.clear();
            //Clearing it should help the other network get GCd and it'll reduce overhead until GC does get it
        }
        setChanged();
    }


    public void rebuildNetwork() {
        //Make a temp list. This will be used for comparisons at the end.
        ArrayList<Long> oldPositions = new ArrayList<>(pipes.keySet());

        //We'll start with a pipe, and expand in all directions similarly to the fluid pump, searching for pipes.
        BlockPos start = BlockPos.of(oldPositions.remove(0));
        TileFluidPipe startPipe = pipes.get(start.asLong());

        //A temp hashmap we can work with. Preserves pipe references whilst we need them.
        HashMap<Long, TileFluidPipe> tempPipes = new HashMap<>(pipes);

        //Clear our list of stored pipes. This will be repopulated.
        pipes.clear();

        //Populate the start in the new hashmap.
        pipes.put(start.asLong(), startPipe);

        //Set our id to the position of the starting pipe. This is important for dealing with split networks.
        this.id = start.asLong();

        //And now, we begin the search.
        ArrayList<BlockPos> toSearch = new ArrayList<>();
        toSearch.add(start);
        while (!toSearch.isEmpty()) {
            //It'll auto break out when it's done.
            for (int i=toSearch.size()-1;i>=0;i--) {
                BlockPos pos = toSearch.get(i);
                for (Direction direction : Direction.values()) {
                    BlockPos pos2 = pos.relative(direction);
                    BlockEntity be = startPipe.getLevel().getBlockEntity(pos2);
                    if (be != null && be instanceof TileFluidPipe newPipe) {
                        oldPositions.remove(pos2.asLong());
                        newPipe.network = this;
                        pipes.put(pos2.asLong(), newPipe);
                    }
                }
                //Nothing next to this block is a pipe anymore! We can remove it, this helps with performance.
                toSearch.remove(i);
            }
        }

        setChanged();
        //If oldPositions is empty, we've successfully rebuilt the network.
        if (oldPositions.isEmpty())return;


        //Otherwise... time to repeat the process, but with new networks. Yay.

        while (!oldPositions.isEmpty()) {
            start = BlockPos.of(oldPositions.remove(0));
            startPipe = tempPipes.get(start.asLong());

            FluidPipeNetwork newNetwork = new FluidPipeNetwork(start.asLong());

            toSearch = new ArrayList<>();
            toSearch.add(start);
            while (!toSearch.isEmpty()) {
                //It'll auto break out when it's done.
                for (int i=toSearch.size()-1;i>=0;i--) {
                    BlockPos pos = toSearch.get(i);
                    for (Direction direction : Direction.values()) {
                        BlockPos pos2 = pos.relative(direction);
                        BlockEntity be = startPipe.getLevel().getBlockEntity(pos2);
                        if (be != null && be instanceof TileFluidPipe newPipe) {
                            oldPositions.remove(pos2.asLong());
                            newPipe.network = newNetwork;
                            newNetwork.pipes.put(pos2.asLong(), newPipe);
                        }
                    }
                    //Nothing next to this block is a pipe anymore! We can remove it, this helps with performance.
                    toSearch.remove(i);
                }
            }
            newNetwork.setChanged();
        }

    }

    public void setChanged() {
        changedPipes = new ArrayList<>(pipes.keySet());
    }

    public boolean hasChanged(BlockPos pos) {
        return changedPipes.remove(pos.asLong());
    }
    
    
}
