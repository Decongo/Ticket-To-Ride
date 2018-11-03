package weber.kaden.myapplication.ui.gameView;

import java.util.List;

import weber.kaden.common.model.DestinationCard;
import weber.kaden.common.model.PlayerColors;
import weber.kaden.common.model.Route;
import weber.kaden.common.model.TrainCard;

public interface GameViewInterface {

    void updateClaimedRoutes(PlayerColors color, List<Route> routes);
    void setNewValues(List<DestinationCard> nDestCards, List<TrainCard> nTrainCards, List<Integer> nPoints);

}
