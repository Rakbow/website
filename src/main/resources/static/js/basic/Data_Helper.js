//将{label, value}选项里value数组转为label数组
const labelEn2valueArray = (xFormat, xFormatSet) => {
    let tmp = [];
    for (let item1 of xFormat) {
        for (let item2 of xFormatSet) {
            if (item1 === item2.labelEn) {
                tmp.push(item2.value);
            }
        }
    }
    return tmp;
}

const value2Label = (value, xSet) => {
    for (let item of xSet) {
        if (value === item.value) {
            return item.label;
        }
    }
};

const label2Value = (label, xSet) => {
    for (let item of xSet) {
        if (label === item.label) {
            return item.value;
        }
    }
}

const value2LabelEnArray = (xFormat, xFormatSet) => {
    let tmp = [];
    for (let item1 of xFormat) {
        for (let item2 of xFormatSet) {
            if (item1 === item2.value) {
                tmp.push(item2.labelEn);
            }
        }
    }
    return tmp;
}

//将date类型转为string类型
function dateToString(date) {
    const year = date.getFullYear();
    let month = date.getMonth() + 1;
    let day = date.getDate();
    month = month > 9 ? month : '0' + month;
    day = day > 9 ? day : '0' + day;
    return year + "/" + month + "/" + day;
};

//日元(100)转为人民币
function JPY2CNY(jpy) {
    get('https://www.chinamoney.com.cn/ags/ms/cm-u-bk-ccpr/CcprHisNew?currency=100JPY/CNY')
        .then(res => {
            const result = {};
            result.price = parseFloat(jpy) * parseFloat(res.records[0].values[0]) / 100;
            result.updateDate = res.records[0].date;
            result.currentExchangeRate = parseFloat(res.records[0].values[0]);
            return result;
        })
}

//根据选项set转换label -> value
const commonLabel2value = (xFormat, xFormatSet) => {
    let tmp = [];
    for (let item1 of xFormat) {
        for (let item2 of xFormatSet) {
            if (item1 === item2.label) {
                tmp.push(item2.value);
            }
        }
    }
    return tmp;
};

const label2value = (xFormat, xFormatSet) => {
    if(xFormat.length !==0 && xFormatSet.length !== 0) {
        let tmp = [];
        for (let item1 of xFormat) {
            for (let item2 of xFormatSet) {
                if (item2.value === item1.value) {
                    tmp.push(item2.value);
                }
            }
        }
        return tmp;
    }
};

//根据图片url获取图片字节大小和长宽
const getImgProp = (url) => {
    let imgProp = {
        imgSize: 0,
        width: 0,
        height: 0
    };
    fetch(url).then(res => {
        res.blob().then(result => {
            imgProp.imgSize = (result.size/1024).toFixed(2);
            var image = new Image();
            image.src = url;
            image.onload = function (){
                imgProp.width = image.naturalWidth;
                imgProp.height = image.naturalHeight;
            }
        })
    });
    return imgProp;
};

/**
 * 根据图片url转为png文件对象
 * @param url
 * @param imageName
 * @returns {Promise<unknown>}
 */
function getImageFileFromUrl(url, imageName) {
    return new Promise((resolve, reject) => {
        let blob = null;
        const xhr = new XMLHttpRequest();
        xhr.open("GET", url);
        xhr.setRequestHeader('Accept', 'image/png');
        xhr.responseType = "blob";
        // 加载时处理
        xhr.onload = () => {
            // 获取返回结果
            blob = xhr.response;
            let imgFile = new File([blob], imageName, { type: 'image/png' });
            // 返回结果
            resolve(imgFile);
        };
        xhr.onerror = (e) => {
            reject(e)
        };
        // 发送
        xhr.send();
    });
}


function countTime(time1, time2) {
    res = second2time(parseInt(time2second(time1)) + parseInt(time2second(time2)));
    console.log(res);
}

function second2time(time) {
    const h = parseInt(time / 3600)
    const minute = parseInt(time / 60 % 60)
    const second = Math.ceil(time % 60)

    const hours = h < 10 ? '0' + h : h
    const formatSecond = second > 59 ? 59 : second
    return `${hours > 0 ? `${hours}:` : ''}${minute < 10 ? '0' + minute : minute}:${formatSecond < 10 ? '0' + formatSecond : formatSecond}`;
}

function time2second(time) {
    if (strCount(time) < 2) {
        time = "00:" + time;
    }
    var hour = time.split(':')[0];
    var min = time.split(':')[1];
    var sec = time.split(':')[2];
    res = Number(hour * 3600) + Number(min * 60) + Number(sec);
    return res;
}

function strCount(str) {
    let target = ":";
    let count = 0
    if (!target) return count
    while (str.match(target)) {
        str = str.replace(target, '')
        count++
    }
    return count
}

function countTotalLength(track_info) {
    var total_length = 0;
    for (let i = 0; i < track_info.disc_list.length; i++) {
        for (let j = 0; j < track_info.disc_list[i].track_list.length; j++) {
            total_length += parseInt(time2second(track_info.disc_list[i].track_list[j].track_length));
        }
    }
    return second2time(total_length);
}

const getSidebarPanelImageClass = (url) => {
    const image = new Image();
    image.src = url;
    return (image.naturalWidth > image.naturalHeight) ? "sidebar-panel-image-middle-width" : "sidebar-panel-image-middle-height";
};

const dropdownValue2Label = (value, set) => {
    for (let item of set) {
        if (item.value === value) {
            return item.label;
        }
    }
}

const initGalleriaImageClass = (url) => {
    const image = new Image();
    image.src = url;
    return (image.naturalWidth > image.naturalHeight) ? "galleria-div-width" : "galleria-div-height";
};

const copy = () => {
    const copyBtn = document.getElementById('copy');
    const clipboard = new Clipboard(copyBtn);//实例化
    clipboard.on('success', e => {
        console.log('复制成功')
        // 释放内存
        clipboard.destroy()
    })
    clipboard.on('error', e => {
        // 不支持复制
        console.log('该浏览器不支持自动复制')
        // 释放内存
        clipboard.destroy()
    })
};

const currencyUnitSet = [
    {labelEn: 'JPY', label: '日元', value: 'JPY'},
    {labelEn: 'CNY', label: '人民币', value: 'CNY'},
    {labelEn: 'USD', label: '美元', value: 'USD'},
    {labelEn: 'EUR', label: '欧元', value: 'EUR'},
    {labelEn: 'TWD', label: '新台币', value: 'TWD'},
    {labelEn: 'OTHER', label: '其他', value: 'UNKNOWN'},
];

const hasBonusSet = [
    {label: "全部", value: null},
    {label: "有", value: 1},
    {label: "无", value: 0}
];

const isLimitedSet = [
    {label: "全部", value: null},
    {label: "是", value: 1},
    {label: "否", value: 0}
];

const isNotForSaleSet = [
    {label: "所有", value: null},
    {label: "是", value: true},
    {label: "否", value: false}
];

const imageTypes = [
    {label: '展示', value: 0},
    {label: '主要', value: 1},
    {label: '其他', value: 2}
];

const getImageTypeLabel = (type) => {
    return value2Label(type, imageTypes);
};

const getNameByCode = (code, regionSet) => {
    for (let region of regionSet) {
        if (region.code === code) {
            return region.name;
        }
    }
}

const responsiveOptions = [
    {
        breakpoint: '1024px',
        numVisible: 5
    },
    {
        breakpoint: '768px',
        numVisible: 3
    },
    {
        breakpoint: '560px',
        numVisible: 1
    }
];

const dbIndexTabs = [
    {
        label: 'Albums',
        icon: 'pi iconfont icon-album',
        to: '/db/albums'
    },
    {
        label: 'Books',
        icon: 'pi iconfont icon-book',
        to: '/db/books'
    },
    {
        label: 'Discs',
        icon: 'pi iconfont icon-Video-Disc',
        to: '/db/discs'
    },
    {
        label: 'Games',
        icon: 'pi iconfont icon-youxi',
        to: '/db/games'
    },
    {
        label: 'Merchs',
        icon: 'pi iconfont icon-yinshuabaozhuang',
        to: '/db/merchs'
    },
];

const dbListTabs = [
    {
        label: 'Albums',
        icon: 'pi iconfont icon-album',
        to: '/db/list/album'
    },
    {
        label: 'Books',
        icon: 'pi iconfont icon-book',
        to: '/db/list/book'
    },
    {
        label: 'Discs',
        icon: 'pi iconfont icon-Video-Disc',
        to: '/db/list/disc'
    },
    {
        label: 'Games',
        icon: 'pi iconfont icon-youxi',
        to: '/db/list/game'
    },
    {
        label: 'Merchs',
        icon: 'pi iconfont icon-yinshuabaozhuang',
        to: '/db/list/merch'
    },
    {
        label: 'Products',
        icon: 'pi pi-th-large',
        to: '/db/list/product'
    },
    {
        label: 'Franchises',
        icon: 'pi pi-th-large',
        to: '/db/list/franchise'
    },
];

const tmpList10 = [{}, {}, {}, {}, {}, {}, {}, {}, {}, {}];
const tmpList5 = [{}, {}, {}, {}, {}];

const indexTabChange = (ev) => {
    dbIndexTabs.forEach(tab => {
        if (ev.index === tab.index) {
            window.location.href=tab.url;
        }
    })
};

const entityTypeValue2Label = (value) => {
    for (let entity of ENTITY_TYPE) {
        if (entity.value === value) {
            return entity.label;
        }
    }
}

const entityTypeValue2Icon = (value) => {
    for (let entity of ENTITY_TYPE) {
        if (entity.value === value) {
            return entity.icon;
        }
    }
}

const entityTypeValue2LabelEn = (value) => {
    for (let entity of ENTITY_TYPE) {
        if (entity.value === value.toString()) {
            return entity.labelEn.toLowerCase();
        }
    }
}

const statusOptions = [false, true];

/**
 * 复制单行内容到粘贴板
 * content : 需要复制的内容
 * message : 复制完后的提示，不传则默认提示"复制成功"
 */
function copyToClip(content) {
    const aux = document.createElement("input");
    aux.setAttribute("value", content);
    document.body.appendChild(aux);
    aux.select();
    document.execCommand("copy");
    document.body.removeChild(aux);
}

const commonValuesToLabels = (values, set) => {
    let labels = [];
    for (let value of values) {
        for(let item of set) {
            if(value === item.value) {
                labels.push(item.label);
            }
        }
    }
    return labels;
}