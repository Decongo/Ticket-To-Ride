package weber.kaden.myapplication.model;
import android.view.Display;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weber.kaden.common.Results;
import weber.kaden.common.command.Command;
import weber.kaden.common.command.CommandData;
import weber.kaden.common.command.CommandFactory;
import weber.kaden.common.command.CommandType;
import weber.kaden.common.model.DestinationCard;
import weber.kaden.common.model.Game;
import weber.kaden.common.model.Model;
import weber.kaden.common.model.Player;
import weber.kaden.common.model.Route;
import weber.kaden.common.model.TrainCard;
import weber.kaden.common.model.TrainCardType;
import weber.kaden.myapplication.serverProxy.ServerProxy;
import java.util.UUID;

public class ClientFacade {

    public void login(String username, String password) throws Exception {
        List<String> credentials = new ArrayList<>(Arrays.asList(username, password));
        CommandData commandData = new CommandData(credentials, CommandType.LOGIN);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if (results == null) {
        	throw new Exception("Login resulted in null");
        }
        if(!results.success()){
            throw new Exception(results.getErrorInfo());
        }

	    Model.getInstance().setCurrentUser(username);
        if (Model.getInstance().getPlayer(username) == null) {
            Model.getInstance().addPlayer(new Player(username,password));
        }
    }

    public void register(String username, String password) throws Exception {
        List<String> credentials = new ArrayList<>(Arrays.asList(username, password));
        CommandData commandData = new CommandData(credentials, CommandType.REGISTER);
        executeLocalCommand(commandData);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if (results == null) {
        	throw new Exception("Register resulted in null");
        }
        if(!results.success()){
            throw new Exception(results.getErrorInfo());
        }

        Model.getInstance().setCurrentUser(username);
    }

    private void executeLocalCommand(CommandData commandData) throws Exception {
        Command command = CommandFactory.getInstance().getCommand(commandData);
        Results results = command.execute();
        if (!results.success()) {
            throw new Exception(results.getErrorInfo());
        }
    }

    public List<Game> getGames(){
        List<String> params = new ArrayList<>(Arrays.asList(Model.getInstance().getCurrentUser()));
        CommandData commandData = new CommandData(params, CommandType.POLLGAMESLIST);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if (results == null || results.getData() == null) {
            return new ArrayList<>();
        }
        return (List<Game>) (results.getData());
    }

    public Results createGame(String username, String gameName) throws Exception {
        List<String> params = new ArrayList<>((Arrays.asList(username, gameName, UUID.randomUUID().toString())));
        CommandData commandData = new CommandData(params, CommandType.CREATEGAME);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if(!results.success()){
            throw new Exception(results.getErrorInfo());
        }
        return results;
    }

    public void joinGame(String username, String gameID) throws Exception {
        List<String> params = new ArrayList<>((Arrays.asList(username, gameID)));
        CommandData commandData = new CommandData(params, CommandType.JOINGAME);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if(!results.success()) {
            throw new Exception(results.getErrorInfo());
        }
        Model.getInstance().updateGame((Game)results.getData());
        Model.getInstance().setCurrentGame((Game)results.getData());
    }

    @Deprecated
    public void startGame(String username, String gameID) throws Exception {
        List<String> params = new ArrayList<>((Arrays.asList(username, gameID)));
        CommandData commandData = new CommandData(params, CommandType.STARTGAME);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if(!results.success()) {
            throw new Exception(results.getErrorInfo());
        }
    }

    public void setupGame(String username, String gameID) throws Exception {
        List<String> params = new ArrayList<>((Arrays.asList(username, gameID)));
        CommandData commandData = new CommandData(params, CommandType.SETUPGAME);

        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if(!results.success()) {
            throw new Exception(results.getErrorInfo());
        }
    }

    public void exitLobby(String username, String gameID) throws Exception {
        List<String> params = new ArrayList<>((Arrays.asList(username, gameID)));
        CommandData commandData = new CommandData(params, CommandType.LEAVEGAME);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if(!results.success()) {
            throw new Exception(results.getErrorInfo());
        }
        Model.getInstance().updateGame(Model.getInstance().getGame(gameID));
        Model.getInstance().setCurrentGame(null);
    }

    public Game getCurrentGame() {
    	return Model.getInstance().getCurrentGame();
    }

    public String getCurrentUser() {
        return Model.getInstance().getCurrentUser();
    }

    public Game getUpdatedGame(Game game) throws Exception{
        List<String> params = new ArrayList<>();
        params.add(getCurrentUser());
        params.add(game.getID());
        CommandData commandData = new CommandData(params, CommandType.POLLGAME);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if (!results.success()) {
            throw new Exception(results.getErrorInfo());
        }
        return (Game) results.getData();
    }

    public void sendMessage(String gameId, String userId, String message) throws Exception{
        List<String> params = new ArrayList<>((Arrays.asList(gameId, userId, message)));
        CommandData commandData = new CommandData(params, CommandType.CHAT);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if(!results.success()) {
            throw new Exception(results.getErrorInfo());
        }
    }
    public void setTravelRateByPlayer(String gameId, String playerId, String numPlaces) throws Exception{
        List<String> params = new ArrayList<>((Arrays.asList(gameId, playerId, numPlaces)));
        CommandData commandData = new CommandData(params, CommandType.SETTRAVELERRATE);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if(!results.success()){
            throw new Exception(results.getErrorInfo());
        }

    }

    public List<DestinationCard> getDealtDestinationCardsForCurrentPlayer() {
        return Model.getInstance().getDealtDestinationCards();
    }
    public List<DestinationCard> getDestinationCardsForTurn(){
        return Model.getInstance().getCurrentGame().getTopOfDestinationCardDeck();
    }

    public boolean PlayerCanClaimRoute(int number, TrainCardType type) {
        return Model.getInstance().PlayerCanClaimRoute(number, type);
    }
    public void sendDestinationCards(String playerId, String gameId, List<DestinationCard> keptCards, List<DestinationCard> discarded) throws Exception{
        List<String> params = new ArrayList<>((Arrays.asList(gameId, playerId)));
        List<Object> data = new ArrayList<>();
        data.add(keptCards);
        data.add(discarded);
        CommandData commandData = new CommandData(params, CommandType.DRAWDESTINATIONCARDS, data);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if(!results.success()){
            throw new Exception(results.getErrorInfo());
        }
    }

    public void chooseTrainCardFromFaceUpCards(String gameId, String playerId, int cardIndex) throws Exception {
        List<String> params = new ArrayList<>(Arrays.asList(gameId, playerId));
        List<Object> data = new ArrayList<>();
        data.add(cardIndex);
        CommandData commandData = new CommandData(params, CommandType.DRAWTRAINCARDFROMFACEUP, data);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if (!results.success()) {
            throw new Exception(results.getErrorInfo());
        }
    }

    public void chooseTrainCardFromDeck(String gameId, String playerId) throws Exception {
        List<String> params = new ArrayList<>(Arrays.asList(gameId, playerId));
        CommandData commandData = new CommandData(params, CommandType.DRAWTRAINCARDFROMDECK);
        Results results = ServerProxy.getInstance().sendCommand(commandData);
        if (!results.success()) {
            throw new Exception(results.getErrorInfo());
        }
    }

    public boolean isCurrentPlayer() {
        return Model.getInstance().isCurrentPlayer();
    }

    public void finishTurn() {
       List<String> params = new ArrayList<>();
       params.add(Model.getInstance().getCurrentGame().getID());
       CommandData commandData = new CommandData(params, CommandType.FINISHTURN);
       Results results = ServerProxy.getInstance().sendCommand(commandData);
    }

    //TESTING METHODS--------------------------------------------------------------------------------------------------------------------

    public void testDrawDestinationCards() {
        List<DestinationCard> cards = Model.getInstance().getCurrentGame().getTopOfDestinationCardDeck();
        List<String> params = new ArrayList<String>();
        params.add(Model.getInstance().getCurrentGame().getID());
        params.add(Model.getInstance().getCurrentUser());
        List<Object> data = new ArrayList<>();
        data.add(cards);
        data.add(null);
        CommandData commandData = new CommandData(params, CommandType.DRAWDESTINATIONCARDS, data);
        ServerProxy.getInstance().sendCommand(commandData);
    }

    public void testDrawTrainCard() {
        TrainCard card = Model.getInstance().getCurrentGame().getTopOfTrainCardDeck();
        List<String> params = new ArrayList<String>();
        params.add(Model.getInstance().getCurrentGame().getID());
        params.add(Model.getInstance().getCurrentUser());
        List<Object> data = new ArrayList<>();
        data.add(card);
        CommandData commandData = new CommandData(params, CommandType.DRAWTRAINCARDFROMDECK, data);
        ServerProxy.getInstance().sendCommand(commandData);
    }

    public void testRemoveTrainCardFromPlayer() {
        TrainCard card = Model.getInstance().getCurrentGame().getPlayer(Model.getInstance().getCurrentUser()).getTrainCards().get(0);
        List<String> params = new ArrayList<String>();
        params.add(Model.getInstance().getCurrentGame().getID());
        params.add(Model.getInstance().getCurrentUser());
        List<Object> data = new ArrayList<>();
        data.add(card);
        CommandData commandData = new CommandData(params, CommandType.DISCARDTRAINCARD, data);
        ServerProxy.getInstance().sendCommand(commandData);
    }

    public void testRemoveDestinationCardFromPlayer() {
        DestinationCard card = Model.getInstance().getCurrentGame().getPlayer(Model.getInstance().getCurrentUser()).getDestinationCardHand().get(0);
        List<String> params = new ArrayList<String>();
        params.add(Model.getInstance().getCurrentGame().getID());
        params.add(Model.getInstance().getCurrentUser());
        List<Object> data = new ArrayList<>();
        data.add(card);
        CommandData commandData = new CommandData(params, CommandType.DISCARDDESTINATIONCARD, data);
        ServerProxy.getInstance().sendCommand(commandData);
    }

    public void testDealTrainCardsToOpponents() {
        for (int i = 0; i < Model.getInstance().getCurrentGame().getPlayers().size(); i++) {
            if (!Model.getInstance().getCurrentGame().getPlayers().get(i).getID().equals(Model.getInstance().getCurrentUser())) {
                TrainCard card = Model.getInstance().getCurrentGame().getTopOfTrainCardDeck();
                List<String> params = new ArrayList<String>();
                params.add(Model.getInstance().getCurrentGame().getID());
                params.add(Model.getInstance().getCurrentGame().getPlayers().get(i).getID());
                List<Object> data = new ArrayList<>();
                data.add(card);
                CommandData commandData = new CommandData(params, CommandType.DRAWTRAINCARDFROMDECK, data);
                ServerProxy.getInstance().sendCommand(commandData);
            }
        }
    }

    public void testChangeOpponentsTrainCars() {
        for (int i = 0; i < Model.getInstance().getCurrentGame().getPlayers().size(); i++) {
            if (!Model.getInstance().getCurrentGame().getPlayers().get(i).getID().equals(Model.getInstance().getCurrentUser())) {
                List<String> params = new ArrayList<String>();
                params.add(Model.getInstance().getCurrentGame().getID());
                params.add(Model.getInstance().getCurrentGame().getPlayers().get(i).getID());
                CommandData commandData = new CommandData(params, CommandType.USETRAINCARS);
                ServerProxy.getInstance().sendCommand(commandData);
            }
        }
    }

    public void testDealDestinationCardsToOpponents() {
        for (int i = 0; i < Model.getInstance().getCurrentGame().getPlayers().size(); i++) {
            if (!Model.getInstance().getCurrentGame().getPlayers().get(i).getID().equals(Model.getInstance().getCurrentUser())) {
                List<DestinationCard> cards = Model.getInstance().getCurrentGame().getPlayers().get(i).getDealtDestinationCards();
                List<String> params = new ArrayList<String>();
                params.add(Model.getInstance().getCurrentGame().getID());
                params.add(Model.getInstance().getCurrentGame().getPlayers().get(i).getID());
                List<Object> data = new ArrayList<>();
                data.add(cards);
                data.add(null);
                CommandData commandData = new CommandData(params, CommandType.DRAWDESTINATIONCARDS, data);
                ServerProxy.getInstance().sendCommand(commandData);
            }
        }
    }

    public void testHaveOpponentClaimRoute(Route route) {
        List<String> params = new ArrayList<>();
        params.add(Model.getInstance().getCurrentGame().getID());
        params.add(Model.getInstance().getCurrentUser());
        List<Object> data = new ArrayList<>();
        data.add(route);
        CommandData commandData = new CommandData(params, CommandType.CLAIMROUTE, data);
        ServerProxy.getInstance().sendCommand(commandData);
    }
}