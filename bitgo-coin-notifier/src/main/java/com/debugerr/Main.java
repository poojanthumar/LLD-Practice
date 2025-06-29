package com.debugerr;

import java.util.*;


// Entitites
class User {
    String name;
    Integer id;
    User(String name, Integer id) {
        this.name = name;
        this.id = id;
    }
    //getters and setters
}


class Notification {
    String coin;
    Double currentPrice;
    String data;

    String getCoin() { return this.coin;}
}

//MANAGERS
class UserManager {
    Map<Integer, User> users;

    public void addUser(Integer id, String name) {
        //validations
        users.put(id, new User(name, id));
    }

    public Set<Integer> getAllUsers() {
        return users.keySet();
    }
}

class CoinManager {
    Map<String, Set<Integer>> coinToUserMap;

    public void addSubsciption(Integer userId, String coin) {
        //validations
        if (coinToUserMap.containsKey(coin))
        {
            coinToUserMap.get(coin).add(userId);
        }
        else coinToUserMap.put(coin, Set.of(userId));
    }

    public void removeSubsciption(Integer userId, String coin) {
        //validations
        if (coinToUserMap.containsKey(coin))
        {
            coinToUserMap.get(coin).remove(userId);
        }
    }

    public void addAllCoinsSubsciption(Integer userId) {
        coinToUserMap.forEach((coin, usersSet) -> {
            usersSet.add(userId);
        });
    }
    public Set<Integer> getAllUsersFromCoin(String coin) {
        return coinToUserMap.get(coin);
    }
}

class NotificationManager {
    Map<Integer, Set<Channel>> userToChannelMap;

    public void addPreference(Integer userId, Channel channel) {
        //validations
        if (userToChannelMap.containsKey(userId))
        {
            userToChannelMap.get(userId).add(channel);
        }
        else userToChannelMap.put(userId, Set.of(channel));
    }

    public void removePreference(Integer userId, Channel channel) {
        //validations
        if (userToChannelMap.containsKey(userId))
        {
            userToChannelMap.get(userId).remove(channel);
        }
    }
    public Set<Channel> getAllChannelsForUser(Integer userId) {
        return userToChannelMap.get(userId);
    }
}


//ONBOARDING Service

class OnboardingService {
    CoinManager coinManager;
    UserManager userManager;
    NotificationManager notificationManager;

    void onBoardUser(Integer id, String name, Set<Channel> channels)
    {
        userManager.addUser(id,name);
        coinManager.addAllCoinsSubsciption(id);
        channels.forEach(channel -> { notificationManager.addPreference(id, channel); } );
    }

    void onBoardCoin(String name) {
        userManager.getAllUsers().forEach(userId -> {
            coinManager.addSubsciption(userId, name);
        });
    }
}


class NotificationService {
    CoinManager coinManager;
    UserManager userManager;
    NotificationManager notificationManager;

    void handleNotification(Notification notification) {
        Set<Integer> users  = coinManager.getAllUsersFromCoin(notification.getCoin());
        users.forEach(user -> {
            Set<Channel> notificationChannels = notificationManager.getAllChannelsForUser(user);
            notificationChannels.forEach(channel -> channel.sendNotification(notification));
        });
    }
}



interface Channel {
    void sendNotification(Notification notification);
}

class EmailChannel implements Channel {
    String mailId;
    public void sendNotification(Notification notification){
        System.out.println("Email NotificationSent");
        //Implementation of sendNotificaiton
    }
}



class Main {

    public static void main (String[] args) {
        System.out.print("hello");
    }

}