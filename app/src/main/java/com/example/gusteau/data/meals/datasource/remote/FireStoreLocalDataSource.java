package com.example.gusteau.data.meals.datasource.remote;

import com.example.gusteau.data.model.FavMealsContainer;
import com.example.gusteau.data.model.FireStoreFavMeal;
import com.example.gusteau.data.model.FireStorePlannedMeal;
import com.example.gusteau.data.model.PlannedMealsContainer;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class FireStoreLocalDataSource {

    private final FirebaseFirestore firestore;

    private static final String FAV_MEALS_COLLECTION = "FavMeals";
    private static final String PLANNED_MEALS_COLLECTION = "planned_meals";

    public FireStoreLocalDataSource() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public Completable updateAllFavMeals(String userId, List<FireStoreFavMeal> meals) {
        return Completable.create(emitter -> {
            List<FireStoreFavMeal> safeList = (meals == null) ? new ArrayList<>() : meals;

            FavMealsContainer container = new FavMealsContainer(safeList);

            firestore.collection(FAV_MEALS_COLLECTION)
                    .document(userId)
                    .set(container)
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<List<FireStoreFavMeal>> getFavMeals(String userId) {
        return Single.create(emitter -> {
            firestore.collection(FAV_MEALS_COLLECTION)
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            FavMealsContainer container = documentSnapshot.toObject(FavMealsContainer.class);
                            if (container != null && container.getMeals() != null) {
                                emitter.onSuccess(container.getMeals());
                            } else {
                                emitter.onSuccess(new ArrayList<>());
                            }
                        } else {
                            emitter.onSuccess(new ArrayList<>());
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }




    public Completable updateAllPlannedMeals(String userId, List<FireStorePlannedMeal> meals) {
        return Completable.create(emitter -> {
            List<FireStorePlannedMeal> safeList = (meals != null) ? meals : new ArrayList<>();

            PlannedMealsContainer container = new PlannedMealsContainer(safeList);

            firestore.collection(PLANNED_MEALS_COLLECTION)
                    .document(userId)
                    .set(container)
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
    public Single<List<FireStorePlannedMeal>> getPlannedMeals(String userId) {
        return Single.create(emitter -> {
            firestore.collection(PLANNED_MEALS_COLLECTION)
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            PlannedMealsContainer container = documentSnapshot.toObject(PlannedMealsContainer.class);
                            if (container != null && container.getMeals() != null) {
                                emitter.onSuccess(container.getMeals());
                            } else {
                                emitter.onSuccess(new ArrayList<>());
                            }
                        } else {
                            emitter.onSuccess(new ArrayList<>());
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
}

