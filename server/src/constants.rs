use serde::Deserialize;
use serde::Serialize;
use sqlx::Type;
use strum::Display;
use strum::EnumIter;
use strum::EnumString;

pub const MIN_PASSWORD_LEN: usize = 8;
pub const PAGE_SIZE: usize = 50;

#[derive(
Debug, Copy, Clone, Eq, PartialEq, Serialize, Deserialize, EnumIter, EnumString, Display, Type,
)]
#[sqlx(type_name = "text")]
pub enum MeasurementUnit {
    Metric,
    Imperial,
    ImperialUS
}

#[derive(
    Debug, Copy, Clone, Eq, PartialEq, Serialize, Deserialize, EnumIter, EnumString, Display, Type,
)]
#[sqlx(type_name = "text")]
pub enum Flag {
    Vegetarian,
    Vegan,
    Drink,
    Alcohol,
    Halal,
    Kosher,
    Meal,
    Liquid,
}

#[derive(
    Debug, Copy, Clone, Eq, PartialEq, Serialize, Deserialize, EnumIter, EnumString, Display, Type,
)]
#[sqlx(type_name = "text")]
pub enum MealTime {
    Breakfast,
    Lunch,
    Dinner,
    Snack,
}

#[derive(
    Debug, Copy, Clone, Eq, PartialEq, Serialize, Deserialize, EnumIter, EnumString, Display, Type,
)]
#[sqlx(type_name = "text")]
pub enum Allergen {
    Celery,
    Gluten,
    Crustaceans,
    Eggs,
    Fish,
    Lupin,
    Milk,
    Molluscs,
    Mustard,
    Nuts,
    Peanuts,
    Sesame,
    Soya,
    Sulphites,
}

#[derive(
    Debug, Copy, Clone, Eq, PartialEq, Serialize, Deserialize, EnumIter, EnumString, Display, Type,
)]
#[allow(clippy::upper_case_acronyms)]
#[sqlx(type_name = "text")]
pub enum Cuisine {
    AncientEgyptian,
    AncientGreek,
    AncientRoman,
    AncientChinese,
    AncientIndian,
    AncientIsraelite,
    AncientEuropean,
    Aztec,
    Byzantine,
    Inca,
    Maya,
    MedievalEuropean,
    Ottoman,
    Soviet,
    Other,
    Unknown,
    Europe,
    America,
    Asia,
    Africa,
    MiddleEast,
    Sami,
    Sikh,
    Judaism,
    Jain,
    Islam,
    Buddhist,
    Christianity,
    Hindu,
    Tasmanian,
    NativeAmerican,
    Inuit,
    Afghanistan,
    Albania,
    Algeria,
    Andorra,
    Angola,
    AntiguaBarbuda,
    Argentina,
    Armenia,
    Australia,
    Austria,
    Azerbaijan,
    Bahamas,
    Bahrain,
    Bangladesh,
    Barbados,
    Belarus,
    Belgium,
    Belize,
    Benin,
    Bhutan,
    Bolivia,
    BosniaHerzegovina,
    Botswana,
    Brazil,
    Brunei,
    Bulgaria,
    BurkinaFaso,
    Burundi,
    CotedIvoire,
    CaboVerde,
    Cambodia,
    Cameroon,
    Canada,
    CAR,
    Chad,
    Chile,
    China,
    Colombia,
    Comoros,
    Congo,
    CostaRica,
    Croatia,
    Cuba,
    Cyprus,
    Czech,
    DRC,
    Denmark,
    Djibouti,
    Dominica,
    DominicanRepublic,
    Ecuador,
    Egypt,
    Salvador,
    EquatorialGuinea,
    Eritrea,
    Estonia,
    Eswatini,
    Ethiopia,
    Fiji,
    Finland,
    France,
    Gabon,
    Gambia,
    Georgia,
    Germany,
    Ghana,
    Greece,
    Grenada,
    Guatemala,
    Guinea,
    GuineaBissau,
    Guyana,
    Haiti,
    Honduras,
    Hungary,
    Iceland,
    India,
    Indonesia,
    Iran,
    Iraq,
    Ireland,
    Israel,
    Italy,
    Jamaica,
    Japan,
    Jordan,
    Kazakhstan,
    Kenya,
    Kiribati,
    Kuwait,
    Kyrgyzstan,
    Laos,
    Latvia,
    Lebanon,
    Lesotho,
    Liberia,
    Libya,
    Liechtenstein,
    Lithuania,
    Luxembourg,
    Madagascar,
    Malawi,
    Malaysia,
    Maldives,
    Mali,
    Malta,
    MarshallIslands,
    Mauritania,
    Mauritius,
    Mexico,
    Micronesia,
    Moldova,
    Monaco,
    Mongolia,
    Montenegro,
    Morocco,
    Mozambique,
    Myanmar,
    Namibia,
    Nauru,
    Nepal,
    Netherlands,
    NewZealand,
    Nicaragua,
    Niger,
    Nigeria,
    NorthKorea,
    NorthMacedonia,
    Norway,
    Oman,
    Pakistan,
    Palau,
    Palestine,
    Panama,
    Papua,
    Paraguay,
    Peru,
    Philippines,
    Poland,
    Portugal,
    Qatar,
    Romania,
    Russia,
    Rwanda,
    SaintKitts,
    SaintLucia,
    SaintVincent,
    Samoa,
    SanMarino,
    SaoTome,
    SaudiArabia,
    Senegal,
    Serbia,
    Seychelles,
    SierraLeone,
    Singapore,
    Slovakia,
    Slovenia,
    SolomonIslands,
    Somalia,
    SouthAfrica,
    SouthKorea,
    SouthSudan,
    Spain,
    SriLanka,
    Sudan,
    Suriname,
    Sweden,
    Syria,
    Tajikistan,
    Tanzania,
    Thailand,
    TimorLeste,
    Togo,
    Tonga,
    Trinidad,
    Tunisia,
    Turkey,
    Turkmenistan,
    Tuvalu,
    Uganda,
    Ukraine,
    UAE,
    UK,
    USA,
    England,
    Scotland,
    Wales,
    Uruguay,
    Uzbekistan,
    Vanuatu,
    Venezuela,
    Vietnam,
    Yemen,
    Zambia,
    Zimbabwe,
}

pub mod categories {
    use crate::constants::categories::FoodSubCategory::*;
    use serde::{Deserialize, Serialize};
    use sqlx::Type;
    use strum::{Display, EnumIter, EnumString};

    #[derive(
        Debug,
        Copy,
        Clone,
        Eq,
        PartialEq,
        Hash,
        Serialize,
        Deserialize,
        EnumIter,
        EnumString,
        Display,
        Type,
    )]
    #[sqlx(type_name = "text")]
    pub enum FoodCategory {
        Dairy,
        ReadyMeal,
        Sauce,
        Meat,
        Bakery,
        SoftDrink,
        Alcohol,
        Fruit,
        Pasta,
        Other,
        Vegetable,
        Vegan,
        DrinkMixes,
        Restaurant,
        Snack,
    }

    #[derive(
        Debug,
        Copy,
        Clone,
        Eq,
        PartialEq,
        Hash,
        Serialize,
        Deserialize,
        EnumIter,
        EnumString,
        Display,
        Type,
    )]
    #[sqlx(type_name = "text")]
    pub enum FoodSubCategory {
        Milk,
        Cheese,
        ForOne,
        ForTwo,
        Side,
        Pasta,
        Curry,
        Whole,
        Berry,
        Shapes,
        Sheets,
        Spaghetti,
        Other,
        Succulent,
        Root,
        Prepared,
        Leafy,
        Cider,
        Beer,
        Spirit,
        Cola,
        Lemonade,
        Fruit,
        Bread,
        Roll,
        Cake,
        Doughnut,
        Biscuit,
        Ham,
        Bacon,
        Chicken,
        Turkey,
        Chorizo,
        Beef,
        SausagePork,
        SausageBeef,
        SausageChicken,
        SausageTurkey,
        SausageOther,
        Dairy,
        Meat,
        Sausage,
        Wine,
        Fish,
        Eggs,
        Chips,
        Lamb,
        Asian,
        Mince,
        Citrus,
        CoffeeSyrup,
        AlcoholSyrup,
        MilkshakeSyrup,
        MilkshakePowder,
        Cordial,
        Squash,
        Energy,
        NoodleCup,
        Burger,
        Sandwich,
        Pizza,
        Soup,
        Burrito,
        Steak,
        Starter,
        Dessert,
        Salad,
        Sides,
        Potato,
        Garlic,
        Flavoured,
        Tinned,
        Italian,
        British,
        American,
        Mexican,
        Indian,
        Chocolate,
        Gummy,
        Lolly,
        Candy,
        Crisps,
        EnergyBar,
        Mints,
        Nuts,
        Popcorn,
        Seeds,
        DriedFruit,
        DriedMeat,
        Pie,
        CoffeeGrounds,
        CoffeePowder,
    }

    impl FoodCategory {
        pub fn sub_cats(&self) -> Vec<FoodSubCategory> {
            match self {
                FoodCategory::Restaurant => vec![
                    Other, Beef, Starter, Salad, Dessert, Pizza, Sides, Soup, Burger, Burrito,
                    Sandwich, Steak, Chips,
                ],
                FoodCategory::Dairy => vec![Other, Milk, Cheese, Eggs],
                FoodCategory::ReadyMeal => vec![
                    Other, Side, NoodleCup, Burger, Asian, Italian, British, American, Mexican,
                    Indian,
                ],
                FoodCategory::Sauce => vec![Other, Pasta, Curry, Asian],
                FoodCategory::Fruit => vec![Other, Whole, Berry, Prepared, Citrus, Tinned],
                FoodCategory::Pasta => vec![Other, Shapes, Sheets, Spaghetti],
                FoodCategory::Other => vec![Other, Salad, Soup],
                FoodCategory::Snack => vec![
                    Other, Chocolate, Gummy, Lolly, Candy, Crisps, EnergyBar, Mints, Nuts, Popcorn,
                    Seeds, DriedFruit, DriedMeat,
                ],
                FoodCategory::Vegetable => {
                    vec![Other, Succulent, Root, Prepared, Leafy, Potato, Tinned]
                }
                FoodCategory::Meat => vec![
                    Other,
                    Ham,
                    Bacon,
                    Fish,
                    SausageOther,
                    SausageBeef,
                    SausagePork,
                    SausageTurkey,
                    SausageChicken,
                    Chicken,
                    Turkey,
                    Lamb,
                    Tinned,
                ],
                FoodCategory::Bakery => vec![
                    Other, Bread, Roll, Cake, Doughnut, Biscuit, Garlic, Flavoured, Pie,
                ],
                FoodCategory::SoftDrink => vec![Other, Cola, Lemonade, Fruit, Energy],
                FoodCategory::Alcohol => vec![Other, Cider, Beer, Wine, Spirit],
                FoodCategory::Vegan => vec![Other, Mince, Bacon, Meat, Sausage],
                FoodCategory::DrinkMixes => vec![
                    Other,
                    AlcoholSyrup,
                    CoffeeSyrup,
                    MilkshakeSyrup,
                    MilkshakePowder,
                    Cordial,
                    Squash,
                    CoffeeGrounds,
                    CoffeePowder,
                ],
            }
        }
    }
}
