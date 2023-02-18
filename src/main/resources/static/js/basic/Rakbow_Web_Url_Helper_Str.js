const DOMAIN_URL = "http://localhost:8083";

//region system

const UPDATE_ITEM_STATUS = "/db/update-item-status";

//endregion

//region page
const DATABASE_INDEX_URL = "/db";
const DATABASE_LIST_URL = "/db/list";

const INDEX_SEARCH_URL = "/db/simpleSearch";

const APP_INDEX_URL = "/app";
const BLOG_INDEX_URL = "/blog";

const GET_INDEX_INIT_DATA_URL = "/db/get-index-init-data";
const GET_LIST_INIT_DATA_URL = "/db/get-list-init-data";

const FRANCHISE_LIST_URL = "/db/franchise/list";
const PRODUCT_LIST_URL = "/db/product/list";

const ALBUM_LIST_URL = "/db/album/list";
const ALBUM_INDEX_URL = "/db/albums";

const DISC_INDEX_URL = "/db/discs";
const DISC_LIST_URL = "/db/disc/list";

const BOOK_INDEX_URL = "/db/books";
const BOOK_LIST_URL = "/db/book/list";

const MERCH_INDEX_URL = "/db/merchs";
const MERCH_LIST_URL = "/db/merch/list";

const GAME_INDEX_URL = "/db/games";
const GAME_LIST_URL = "/db/game/list";

//endregion

//region album
const ALBUM_DETAIL_URL = "/db/album/";
const DELETE_ALBUM_URL = "/db/album/delete";
const UPDATE_ALBUM_URL = "/db/album/update";
const INSERT_ALBUM_URL = "/db/album/add";
const GET_ALBUMS_URL = "/db/album/get-albums";

const UPDATE_ALBUM_ARTISTS_URL = "/db/album/update-artists";
const UPDATE_ALBUM_TRACK_INFO_URL = "/db/album/update-trackInfo";
const INSERT_ALBUM_IMAGES_URL = "/db/album/add-images";
const UPDATE_ALBUM_IMAGES_URL = "/db/album/update-images";
const UPDATE_ALBUM_DESCRIPTION_URL = "/db/album/update-description";
const UPDATE_ALBUM_BONUS_URL = "/db/album/update-bonus";
//endregion

//region music
const MUSIC_DETAIL_URL = "/db/music/";
const UPLOAD_MUSIC_FILE_URL = "/db/music/upload-file";
const DELETE_MUSIC_FILE_URL = "/db/music/delete-file";
const UPDATE_MUSIC_URL = "/db/music/update";
const UPDATE_MUSIC_ARTISTS_URL = "/db/music/update-artists";
const UPDATE_MUSIC_LYRICS_TEXT_URL = "/db/music/update-lyrics-text";
const UPDATE_MUSIC_DESCRIPTION_URL = "/db/music/update-description";
//endregion

//region user
const CHECK_USER_AUTHORITY_URL = "/user/check-authority";
const LOGIN_URL = "/login";
const LOGOUT_URL = "/logout";
//endregion

//region product
const PRODUCT_DETAIL_URL = "/db/product/";
const GET_PRODUCTS_URL = "/db/product/get-products";
const GET_PRODUCT_SET_URL = "/db/product/get-product-set";
const ADD_PRODUCT_URL = "/db/product/add";
const UPDATE_PRODUCT_URL = "/db/product/update";
const UPDATE_PRODUCT_ORGANIZATIONS_URL = "/db/product/update-organizations";
const UPDATE_PRODUCT_DESCRIPTION_URL = "/db/product/update-description";
const UPDATE_PRODUCT_STAFF_URL = "/db/product/update-staffs";
const INSERT_PRODUCT_IMAGES_URL = "/db/product/add-images";
const UPDATE_PRODUCT_IMAGES_URL = "/db/product/update-images";
//endregion

//region disc
const DISC_DETAIL_URL = "/db/disc/";
const DELETE_DISC_URL = "/db/disc/delete";
const UPDATE_DISC_URL = "/db/disc/update";
const INSERT_DISC_URL = "/db/disc/add";
const GET_DISCS_URL = "/db/disc/get-discs";

const INSERT_DISC_IMAGES_URL = "/db/disc/add-images";
const UPDATE_DISC_IMAGES_URL = "/db/disc/update-images";
const UPDATE_DISC_SPEC_URL = "/db/disc/update-spec";
const UPDATE_DISC_DESCRIPTION_URL = "/db/disc/update-description";
const UPDATE_DISC_BONUS_URL = "/db/disc/update-bonus";
//endregion

//region book
const BOOK_DETAIL_URL = "/db/book/";
const DELETE_BOOK_URL = "/db/book/delete";
const UPDATE_BOOK_URL = "/db/book/update";
const INSERT_BOOK_URL = "/db/book/add";
const GET_BOOKS_URL = "/db/book/get-books";

const BOOK_ISBN_INTERCONVERT = "/db/book/isbn-interconvert";

const INSERT_BOOK_IMAGES_URL = "/db/book/add-images";
const UPDATE_BOOK_IMAGES_URL = "/db/book/update-images";
const UPDATE_BOOK_AUTHORS_URL = "/db/book/update-authors";
const UPDATE_BOOK_SPEC_URL = "/db/book/update-spec";
const UPDATE_BOOK_DESCRIPTION_URL = "/db/book/update-description";
const UPDATE_BOOK_BONUS_URL = "/db/book/update-bonus";
//endregion

//region merch
const MERCH_DETAIL_URL = "/db/merch/";
const DELETE_MERCH_URL = "/db/merch/delete";
const UPDATE_MERCH_URL = "/db/merch/update";
const INSERT_MERCH_URL = "/db/merch/add";
const GET_MERCHS_URL = "/db/merch/get-merchs";

const INSERT_MERCH_IMAGES_URL = "/db/merch/add-images";
const UPDATE_MERCH_IMAGES_URL = "/db/merch/update-images";
const UPDATE_MERCH_SPEC_URL = "/db/merch/update-spec";
const UPDATE_MERCH_DESCRIPTION_URL = "/db/merch/update-description";
//endregion

//region game
const GAME_DETAIL_URL = "/db/game/";
const DELETE_GAME_URL = "/db/game/delete";
const UPDATE_GAME_URL = "/db/game/update";
const INSERT_GAME_URL = "/db/game/add";
const GET_GAMES_URL = "/db/game/get-games";

const INSERT_GAME_IMAGES_URL = "/db/game/add-images";
const UPDATE_GAME_IMAGES_URL = "/db/game/update-images";
const UPDATE_GAME_ORGANIZATIONS_URL = "/db/game/update-organizations";
const UPDATE_GAME_STAFFS_URL = "/db/game/update-staffs";
const UPDATE_GAME_DESCRIPTION_URL = "/db/game/update-description";
const UPDATE_GAME_BONUS_URL = "/db/game/update-bonus";
//endregion

//region franchise
const FRANCHISE_DETAIL_URL = "/db/franchise/";
const DELETE_FRANCHISE_URL = "/db/franchise/delete";
const UPDATE_FRANCHISE_URL = "/db/franchise/update";
const ADD_FRANCHISE_URL = "/db/franchise/add";
const GET_FRANCHISES_URL = "/db/franchise/get-franchises";

const INSERT_FRANCHISE_IMAGES_URL = "/db/franchise/add-images";
const UPDATE_FRANCHISE_IMAGES_URL = "/db/franchise/update-images";
const UPDATE_FRANCHISE_DESCRIPTION_URL = "/db/franchise/update-description";

//endregion

//region header

const ENTITY = {
    ALBUM: 1,
    DISC: 2,
    BOOK: 3,
    MERCH: 4,
    GAME: 5,
    FRANCHISE: 6,
    PRODUCT: 7,
};

const ENTITY_TYPE = [
    {label: '专辑', labelEn: 'Album', value: '1', icon: 'pi iconfont icon-album'},
    {label: 'BD/DVD', labelEn: 'Disc', value: '2', icon: 'pi iconfont icon-Video-Disc'},
    {label: '书籍', labelEn: 'Book', value: '3', icon: 'pi iconfont icon-book'},
    {label: '周边', labelEn: 'Merch', value: '4', icon: 'pi iconfont icon-yinshuabaozhuang'},
    {label: '游戏', labelEn: 'Game', value: '5', icon: 'pi iconfont icon-youxi'},
    {label: '系列', labelEn: 'Franchise', value: '6', icon: 'pi pi-bookmark'},
    {label: '作品', labelEn: 'Product', value: '7', icon: 'pi pi-th-large'},
    {label: '文章', labelEn: 'Article', value: '8', icon: 'pi pi-bars'}
];

const NOT_LOGIN_NAVBAR_ITEMS = [
    {
        label: '首页', icon: 'pi pi-fw pi-home', url: DOMAIN_URL
    },
    {
        label: '数据库',
        icon: 'pi pi-fw iconfont icon-database',
        items: [
            {
                label: '数据库首页',
                icon: 'pi pi-fw iconfont icon-database',
                url: DATABASE_INDEX_URL
            },
            {
                label: '数据库(列表)',
                icon: 'pi pi-fw iconfont icon-database',
                url: DATABASE_LIST_URL
            }
        ]
    },
    // {
    //     label: '博客', icon: 'pi pi-fw pi-book', url: BLOG_INDEX_URL
    // },
    // {
    //     label: '应用', icon: 'pi iconfont icon-yingyong', url: APP_INDEX_URL
    // },
    {
        label: '后台', icon: 'pi iconfont icon-login', url: LOGIN_URL
    }
];

const LOGIN_NAVBAR_ITEMS = [
    {
        label: '首页',
        icon: 'pi pi-fw pi-home',
        url: DOMAIN_URL
    },
    {
        label: '数据库',
        icon: 'pi pi-fw iconfont icon-database',
        items: [
            {
                label: '数据库首页',
                icon: 'pi pi-fw iconfont icon-database',
                url: DATABASE_INDEX_URL
            },
            {
                label: '数据库(列表)',
                icon: 'pi pi-fw iconfont icon-database',
                url: DATABASE_LIST_URL
            }
        ]
    },
    // {
    //     label: '博客',
    //     icon: 'pi pi-fw pi-book',
    //     url: BLOG_INDEX_URL
    // },
    // {
    //     label: '应用',
    //     icon: 'pi iconfont icon-yingyong',
    //     url: APP_INDEX_URL
    // },
    {
        label: '登出', icon: 'pi iconfont icon-logout', url: LOGOUT_URL
    }
];
//endregion