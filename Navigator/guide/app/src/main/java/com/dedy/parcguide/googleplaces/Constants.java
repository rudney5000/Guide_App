package com.dedy.parcguide.googleplaces;

import com.dedy.parcguide.R;
import com.dedy.parcguide.settings.AppSettings;

import java.util.ArrayList;
import java.util.List;


public class Constants {

    public static final String PLACE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=%d&photoreference=%s&key=%s";

    public static final String NEARBY_PLACES_URL = "https://maps.googleapis.com/maps/api/place/search/json";

    public static final String GOOGLE_PLUS_URL = "https://www.googleapis.com/plus/v1/people/";

    /**
     * Default value
     */
    public static int DEFAULT_PHOTO_WIDTH = 540;

    public enum PLACE_TYPES {

        GOOGLE_PLACES_SLEEP("Sleep in", R.color.ab_color_green, "lodging"),

        GOOGLE_PLACES_EAT("Eat in", R.color.ab_color_blue, "bakery|meal_delivery|meal_takeaway|food|restaurant"),

        GOOGLE_PLACES_ENJOY("Enjoy in", R.color.ab_color_purple, "cafe|bar|night_club"),

        GOOGLE_PLACES_FAV("Favourite in", R.color.ab_color_fav, "");

        /**
         * Title of the type. Set in the actionbar in list places screen
         */
        private String title;
        /**
         * The color for the current type. Use to set actionbar in the list screen and other views if required
         */
        int resourceId;
        /**
         * Google type. We use this to create the URL for fetching the places
         */
        String type;

        PLACE_TYPES(String title, int resourceId, String value) {
            this.title = title;
            this.resourceId = resourceId;
            this.type = value;
        }

        public static List<String> getAllTypesAsList() {

            List<String> result = new ArrayList<String>();

            for (int i = 0; i < PLACE_TYPES.values().length; i++) {
                result.add(PLACE_TYPES.values()[i].getType());
            }

            return result;
        }

        public static PLACE_TYPES isValueInAnyType(String value) {


            for (int i = 0; i < PLACE_TYPES.values().length; i++) {
                if (values()[i].getType().toString().indexOf(value) > -1) {
                    return values()[i];
                }
            }

            return null;
        }

        public static int getFullRatingImageForType(PLACE_TYPES type) {
            if (type == GOOGLE_PLACES_SLEEP) {

                return R.drawable.ic_rate_sleep_full;

            } else if (type == GOOGLE_PLACES_EAT) {
                return R.drawable.ic_rate_eat_full;

            } else if (type == GOOGLE_PLACES_ENJOY) {
                return R.drawable.ic_rate_enjoy_full;
            } else if (type == GOOGLE_PLACES_FAV) {
                return R.drawable.ic_rate_fav_full;
            }

            return 0;
        }

        public static int getHalfRatingImageForType(PLACE_TYPES type) {
            if (type == GOOGLE_PLACES_SLEEP) {

                return R.drawable.ic_rate_sleep_half;

            } else if (type == GOOGLE_PLACES_EAT) {
                return R.drawable.ic_rate_eat_half;

            } else if (type == GOOGLE_PLACES_ENJOY) {
                return R.drawable.ic_rate_enjoy_half;
            } else if (type == GOOGLE_PLACES_FAV) {
                return R.drawable.ic_rate_fav_half;
            }

            return 0;
        }

        public static int getFavIconEmptyId(PLACE_TYPES type) {
            if (type == GOOGLE_PLACES_SLEEP) {

                return R.drawable.selector_fav_sleep;

            } else if (type == GOOGLE_PLACES_EAT) {
                return R.drawable.selector_fav_eat;

            } else if (type == GOOGLE_PLACES_ENJOY) {
                return R.drawable.selector_fav_enjoy;
            } else if (type == GOOGLE_PLACES_FAV) {
                return R.drawable.selector_favourite;
            }

            return 0;
        }

        public static int getIconMap(PLACE_TYPES type) {
            if (type == GOOGLE_PLACES_SLEEP) {

                return R.drawable.ic_sleep_map;
            } else if (type == GOOGLE_PLACES_EAT) {

                return R.drawable.ic_eat_map;
            } else if (type == GOOGLE_PLACES_ENJOY) {

                return R.drawable.ic_enjoy_map;
            }else if(type == GOOGLE_PLACES_FAV){

                return R.drawable.ic_fav_map;
            }

            return 0;
        }

        public static int getIconPhone(PLACE_TYPES type) {
            if (type == GOOGLE_PLACES_SLEEP) {

                return R.drawable.ic_sleep_call;

            } else if (type == GOOGLE_PLACES_EAT) {
                return R.drawable.ic_eat_call;

            } else if (type == GOOGLE_PLACES_ENJOY) {
                return R.drawable.ic_enjoy_call;
            } else if (type == GOOGLE_PLACES_FAV) {
                return R.drawable.ic_fav_call;
            }

            return 0;
        }

        public static int getIconEye(PLACE_TYPES type) {
            if (type == GOOGLE_PLACES_SLEEP) {

                return R.drawable.ic_sleep_view;

            } else if (type == GOOGLE_PLACES_EAT) {
                return R.drawable.ic_eat_view;

            } else if (type == GOOGLE_PLACES_ENJOY) {
                return R.drawable.ic_enjoy_view;
            } else if (type == GOOGLE_PLACES_FAV) {
                return R.drawable.ic_fav_view;
            }

            return 0;
        }

        public static int getEmptyReviewImage(PLACE_TYPES type) {

            if (type == GOOGLE_PLACES_SLEEP) {

                return R.drawable.ic_sleep_ratings_user_pic;

            } else if (type == GOOGLE_PLACES_EAT) {
                return R.drawable.ic_eat_ratings_user_pic;

            } else if (type == GOOGLE_PLACES_ENJOY) {
                return R.drawable.ic_enjoy_ratings_user_pic;
            }

            return 0;
        }

        public static int getTagsBg(PLACE_TYPES type) {

            if (type == GOOGLE_PLACES_SLEEP) {

                return R.drawable.tags_bg_green;

            } else if (type == GOOGLE_PLACES_EAT) {
                return R.drawable.tags_bg_blue;

            } else if (type == GOOGLE_PLACES_ENJOY) {
                return R.drawable.tags_bg_purple;
            } else if (type == GOOGLE_PLACES_FAV) {
                return R.drawable.tags_bg_fav;
            }

            return 0;
        }

        public static int getSearchIcon(PLACE_TYPES type) {

            if (type == GOOGLE_PLACES_SLEEP) {

                return R.drawable.ic_search_sleep;

            } else if (type == GOOGLE_PLACES_EAT) {
                return R.drawable.ic_search_eat;

            } else if (type == GOOGLE_PLACES_ENJOY) {
                return R.drawable.ic_search_enjoy;
            }

            return 0;
        }

        public static int getSearchAutoTextBg(PLACE_TYPES type) {

            if (type == GOOGLE_PLACES_SLEEP) {

                return R.drawable.et_half_border_green;

            } else if (type == GOOGLE_PLACES_EAT) {
                return R.drawable.et_half_border_blue;

            } else if (type == GOOGLE_PLACES_ENJOY) {
                return R.drawable.et_half_border_purple;
            }

            return 0;
        }

        public static int getEmptyImagePlaceHolder(PLACE_TYPES type) {

            if (type == GOOGLE_PLACES_SLEEP) {

                return R.drawable.sleep_placeholder;
            } else if (type == GOOGLE_PLACES_EAT) {

                return R.drawable.eat_placeholder;
            } else if (type == GOOGLE_PLACES_ENJOY) {

                return R.drawable.enjoy_placeholder;
            } else if (type == GOOGLE_PLACES_FAV) {

                return R.drawable.fav_placeholder;
            }

            return 0;
        }

        public String getTitle() {
            return title + " " + AppSettings.TOWN;
        }

        public int getColourId() {
            return resourceId;
        }

        public String getType() {
            return type;
        }
    }
}
