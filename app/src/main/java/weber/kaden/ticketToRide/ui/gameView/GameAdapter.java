package weber.kaden.ticketToRide.ui.gameView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ToggleButton;

import java.util.List;

import weber.kaden.common.model.DestinationCard;
import weber.kaden.ticketToRide.R;
import weber.kaden.ticketToRide.ui.tools.ConstantsDisplayConverter;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder>{
    private List<DestinationCard> destinationCards;
    //private  List<TrainCard> trainCards;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    GameAdapter(Context context, List<DestinationCard> destinationCards) {
        this.mInflater = LayoutInflater.from(context);
        this.destinationCards = destinationCards;
        //this.trainCards = trainCards;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.individual_card_for_map, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConstantsDisplayConverter converter = new ConstantsDisplayConverter();
        DestinationCard destinationCard =  destinationCards.get(position);
        String destinationText = converter.getUIStringFor(destinationCard.getStartCity()) + "\nTo\n"
                + converter.getUIStringFor(destinationCard.getEndCity());
        holder.toggleDest.setTextOn(destinationText);
        holder.toggleDest.setTextOff(destinationText);
        holder.toggleDest.setText(destinationText);
        holder.toggleDest.setEnabled(false);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return destinationCards.size();
    }
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ToggleButton toggleDest;
        ViewHolder(View itemView) {
            super(itemView);
            toggleDest = itemView.findViewById(R.id.destination_card_generic);
        }
    }
}
