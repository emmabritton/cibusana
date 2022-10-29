pub type ErrorNum = i64;

//TODO this should be an enum

pub const EMAIL_ALREADY_TAKEN: ErrorNum = 100;
pub const EMAIL_INVALID: ErrorNum = 101;
pub const NAME_EMPTY: ErrorNum = 102;
pub const EMAIL_EMPTY: ErrorNum = 103;
pub const PASSWORD_EMPTY: ErrorNum = 104;
pub const PASSWORD_TOO_SHORT: ErrorNum = 105;
pub const INVALID_LOGIN: ErrorNum = 106;
pub const INVALID_FILTER_COMBO: ErrorNum = 107;
pub const INVALID_FILTER_CATEGORY: ErrorNum = 108;
pub const ID_EMPTY: ErrorNum = 109;
pub const CALORIES_EMPTY: ErrorNum = 110;
pub const CARBS_EMPTY: ErrorNum = 111;
pub const FAT_EMPTY: ErrorNum = 112;
pub const PROTEIN_EMPTY: ErrorNum = 113;
pub const CATEGORY_EMPTY: ErrorNum = 114;
pub const SUBCATEGORY_EMPTY: ErrorNum = 115;
pub const CATEGORY_INVALID: ErrorNum = 116;
pub const SUBCATEGORY_INVALID: ErrorNum = 117;
pub const SUBCATEGORY_MISMATCH: ErrorNum = 118;
pub const TOKEN_EMPTY: ErrorNum = 119;
pub const TOKEN_INVALID: ErrorNum = 120;
pub const CONTENT_EMPTY: ErrorNum = 121;
pub const ID_INVALID: ErrorNum = 122;
pub const INVALID_USER: ErrorNum = 123;
pub const INVALID_FLAG: ErrorNum = 124;
pub const INVALID_ALLERGEN: ErrorNum = 125;
pub const CALORIES_MAX_TOO_LOW: ErrorNum = 126;
pub const CALORIES_TOO_LOW: ErrorNum = 127;
pub const CARBS_MAX_TOO_LOW: ErrorNum = 128;
pub const FAT_MAX_TOO_LOW: ErrorNum = 129;
pub const PROTEIN_MAX_TOO_LOW: ErrorNum = 130;
pub const INVALID_SERVING_SIZE: ErrorNum = 131;
pub const INVALID_CALORIES: ErrorNum = 132;
pub const CARBS_NEGATIVE: ErrorNum = 133;
pub const FAT_NEGATIVE: ErrorNum = 134;
pub const PROTEIN_NEGATIVE: ErrorNum = 135;
pub const INVALID_CUISINE: ErrorNum = 136;
pub const INVALID_INGREDIENT: ErrorNum = 137;
pub const INVALID_ID: ErrorNum = 138;
pub const SERVER_ERROR: ErrorNum = 500;

pub fn format_error(codes: &[ErrorNum]) -> String {
    let mut output = String::new();
    for code in codes {
        output.push_str(error_text(*code));
        output.push('\n');
    }
    output
}

pub fn error_text(code: ErrorNum) -> &'static str {
    match code {
        EMAIL_ALREADY_TAKEN => "Email address already taken",
        EMAIL_INVALID => "Email address is invalid",
        NAME_EMPTY => "Name must not be empty",
        EMAIL_EMPTY => "Email must not be empty",
        PASSWORD_EMPTY => "Password must not be empty",
        PASSWORD_TOO_SHORT => "Password is too short, must be at least 8 characters",
        INVALID_LOGIN => "Email/Password invalid",
        SERVER_ERROR => "Server error, please try again in a bit",
        INVALID_FILTER_COMBO => "Can't use general with any other filter query",
        INVALID_FILTER_CATEGORY => "Can't use subcategory without category",
        ID_EMPTY => "ID must be a valid UUID",
        ID_INVALID => "ID must be a valid UUID",
        CALORIES_EMPTY => "Calories must not be empty",
        CARBS_EMPTY => "Calories must not be empty",
        FAT_EMPTY => "Calories must not be empty",
        PROTEIN_EMPTY => "Calories must not be empty",
        CATEGORY_EMPTY => "Category must not be empty",
        SUBCATEGORY_EMPTY => "Subcategory must not be empty",
        CATEGORY_INVALID => "Category is not valid",
        SUBCATEGORY_INVALID => "Subcategory is not valid",
        SUBCATEGORY_MISMATCH => "Subcategory not valid for selected category",
        TOKEN_EMPTY => "Token must be a valid UUID",
        TOKEN_INVALID => "Token must be a valid UUID",
        CONTENT_EMPTY => "Content must not be empty",
        INVALID_USER => "User token unauthorized",
        INVALID_FLAG => "Flag is not valid",
        INVALID_ALLERGEN => "Allergen is not valid",
        CALORIES_MAX_TOO_LOW => "Max <= Min for calories",
        CALORIES_TOO_LOW => "Calories must be >= 101",
        CARBS_MAX_TOO_LOW => "Max <= Min for carbs",
        FAT_MAX_TOO_LOW => "Max <= Min for fat",
        INVALID_SERVING_SIZE => "Invalid serving size",
        INVALID_CALORIES => "Invalid calories",
        PROTEIN_MAX_TOO_LOW => "Max <= Min for protein",
        CARBS_NEGATIVE => "Invalid carbs as negative",
        FAT_NEGATIVE => "Invalid fat as negative",
        PROTEIN_NEGATIVE => "Invalid protein as negative",
        INVALID_CUISINE => "Invalid cuisine",
        INVALID_INGREDIENT => "Invalid ingredient",
        INVALID_ID => "Invalid ID",
        _ => "",
    }
}
