import {HttpUtil} from '/js/basic/Http_Util.js';
import {RKW_Web} from '/js/basic/Rakbow_Web_Control_Strs_CN.js';

export const showBonusEditDialog = (toast, dialog, detailInfo, bonus, images) => {
    const dialogRef = dialog.open(bonusEditPanel, {
        props: {
            header: '特典信息',
            style: {
                width: '80vw',
            },
            breakpoints: {
                '960px': '75vw',
                '640px': '90vw'
            },
            modal: true
        },
        data: {
            detailInfo: detailInfo,
            images: images,
            bonus: bonus,
            toast: toast,
        },
    });
};

export const showDescriptionEditDialog = (toast, dialog, detailInfo, images) => {
    const dialogRef = dialog.open(descriptionEditPanel, {
        props: {
            header: '描述信息',
            style: {
                width: '80vw',
            },
            breakpoints: {
                '960px': '75vw',
                '640px': '90vw'
            },
            modal: true
        },
        data: {
            detailInfo: detailInfo,
            toast: toast,
            images: images,
        },
    });
};

export const showSpecEditDialog = (toast, dialog, entityId, entityType, items, itemSet) => {
    const dialogRef = dialog.open(specEditPanel, {
        props: {
            header: '规格信息编辑',
            style: {
                width: '60vw',
            },
            breakpoints: {
                '960px': '55vw',
                '640px': '70vw'
            },
            modal: true
        },
        data: {
            entityId: entityId,
            entityType: entityType,
            items: items,
            itemSet: itemSet,
            toast: toast,
        },
    });
};

export const showCompaniesEditDialog = (toast, dialog, entityId, entityType, companies, companyRoleSet, companySet) => {
    const dialogRef = dialog.open(companiesEditPanel, {
        props: {
            header: '关联企业编辑',
            style: {
                width: '60vw',
            },
            breakpoints: {
                '960px': '55vw',
                '640px': '70vw'
            },
            modal: true
        },
        data: {
            entityId: entityId,
            entityType: entityType,
            companies: companies,
            companyRoleSet: companyRoleSet,
            companySet: companySet,
            toast: toast,
        },
    });
};

export const showPersonnelEditDialog = (toast, dialog, entityId, entityType, items, roleSet, personnelSet) => {
    const dialogRef = dialog.open(personnelEditPanel, {
        props: {
            header: '关联人员编辑',
            style: {
                width: '60vw',
            },
            breakpoints: {
                '960px': '55vw',
                '640px': '70vw'
            },
            modal: true
        },
        data: {
            entityId: entityId,
            entityType: entityType,
            items: items,
            roleSet: roleSet,
            personnelSet: personnelSet,
            toast: toast,
        },
    });
};

const bonusEditPanel = {
    template: `
        <p-blockui :blocked="editBlock">
            <md-editor-v3 v-model="bonusMd" preview-theme="github"></md-editor-v3>
            <div class="text-end mt-3 mb-2">
                <p-button icon="pi pi-times" label="取消" @click="closeBonusEditDialog"
                      class="p-button-text"></p-button>
                <p-button icon="pi pi-save" label="保存" @click="submitBonus"></p-button>
            </div>
            <p-panel>
                <div v-if="images.length != 0">
                    <p-datatable :value="images" class="p-datatable-sm" striped-rows>
                        <template #header>
                            <div class="flex flex-wrap align-items-center justify-content-between gap-2">
                                <span class="text-xl text-900 font-bold ml-10">图片列表</span>
                            </div>
                        </template>
                        <p-column header-style="width: 5%">
                        <template #body="slotProps">
                            <p-button icon="pi pi-copy" @click="copyImageUrl(slotProps.data.url)"></p-button>
                        </template>
                        </p-column>
                        <p-column header="图片" header-style="width: 8%">
                            <template #body="slotProps">
                                <img :src="slotProps.data.thumbUrl50" :alt="slotProps.data.nameEn"
                                     class="edit-image"/>
                            </template>
                        </p-column>
                        <p-column field="nameZh" header="名(中)" header-style="width: 10%"></p-column>
                        <p-column field="nameEn" header="名(英)" header-style="width: 10%"></p-column>
                        <p-column field="description" header="描述" header-style="width: 20%"></p-column>
                    </p-datatable>
                </div>
                <div v-else>
                    <span class="emptyInfo"><em>暂无图片</em></span>
                </div>
            </p-panel>
        </p-blockui>
    `,
    inject: ['dialogRef'],
    data() {
        return {
            detailInfo: {},
            images: [],
            toast: null,
            editBlock: false,
            bonusMd: "",
        }
    },
    mounted() {
        this.toast = this.dialogRef.data.toast;
        this.detailInfo = this.dialogRef.data.detailInfo;
        this.images = this.dialogRef.data.images;
        this.bonusMd = this.dialogRef.data.bonus;
    },
    watch: {

    },
    methods: {
        copyImageUrl(url) {
            copyToClip(url + '?imageMogr2/auto-orient/thumbnail/400x400');
        },
        closeBonusEditDialog() {
            this.dialogRef.close();
        },
        submitBonus() {
            this.editBlock = true;
            let json = {
                entityType: this.detailInfo.entityType,
                entityId: this.detailInfo.id,
                bonus: this.bonusMd
            };
            HttpUtil.commonVueSubmit(this.toast, UPDATE_BONUS_URL, json)
                .then(res => {
                    if (res.state === 1) {
                        this.dialogRef.close();
                        location.reload(true);
                    }else {
                        this.editBlock = false;
                    }
                });
        }
    },
    components: {
        "p-button": primevue.button,
        "p-blockui": primevue.blockui,
        "p-datatable": primevue.datatable,
        "p-column": primevue.column,
        "p-panel": primevue.panel,
    }
};

const descriptionEditPanel = {
    template: `
    <p-blockui :blocked="editBlock">
    <md-editor-v3 v-model="descriptionMd" preview-theme="github"></md-editor-v3>
    <div class="text-end mt-3 mb-2">
        <p-button icon="pi pi-times" label="取消" @click="closeDescriptionEditDialog"
              class="p-button-text"></p-button>
        <p-button icon="pi pi-save" label="保存" @click="submitDescription"></p-button>
    </div>
    <p-panel>
        <div v-if="images.length != 0">
            <p-datatable :value="images" class="p-datatable-sm" striped-rows>
                <template #header>
                    <div class="flex flex-wrap align-items-center justify-content-between gap-2">
                        <span class="text-xl text-900 font-bold">图片列表</span>
                    </div>
                </template>
                <p-column header-style="width: 5%">
                <template #body="slotProps">
                    <p-button icon="pi pi-copy" @click="copyImageUrl(slotProps.data.url)"></p-button>
                </template>
                </p-column>
                <p-column header="图片" header-style="width: 8%">
                    <template #body="slotProps">
                        <img :src="slotProps.data.thumbUrl50" :alt="slotProps.data.nameEn"
                             class="edit-image"/>
                    </template>
                </p-column>
                <p-column field="nameZh" header="名(中)" header-style="width: 10%"></p-column>
                <p-column field="nameEn" header="名(英)" header-style="width: 10%"></p-column>
                <p-column field="description" header="描述" header-style="width: 20%"></p-column>
            </p-datatable>
        </div>
        <div v-else>
            <span class="emptyInfo"><em>暂无图片</em></span>
        </div>
    </p-panel>
</p-blockui>
    `,
    inject: ['dialogRef'],
    data() {
        return {
            toast: null,
            detailInfo: {},
            images: [],
            editBlock: false,
            descriptionMd: "",

        }
    },
    mounted() {
        console.log(this.itemImageInfo)
        this.detailInfo = this.dialogRef.data.detailInfo;
        this.images = this.dialogRef.data.images;
        this.toast = this.dialogRef.data.toast;
        this.descriptionMd = this.detailInfo.description;
    },
    watch: {

    },
    methods: {
        copyImageUrl(url) {
            copyToClip(url + '?imageMogr2/auto-orient/thumbnail/400x400');
        },
        closeDescriptionEditDialog() {
            this.dialogRef.close();
        },
        submitDescription() {
            this.editBlock = true;
            let json = {
                entityType: this.detailInfo.entityType,
                entityId: this.detailInfo.id,
                description: this.descriptionMd
            }
            HttpUtil.commonVueSubmit(this.toast, UPDATE_DESCRIPTION_URL, json)
                .then(res => {
                    if (res.state === 1) {
                        this.dialogRef.close();
                        location.reload(true);
                    }else {
                        this.editBlock = false;
                    }
                });
        }
    },
    components: {
        "p-button": primevue.button,
        "p-blockui": primevue.blockui,
        "p-datatable": primevue.datatable,
        "p-column": primevue.column,
        "p-panel": primevue.panel,
    }
};

const specEditPanel = {
    template: `
        <p-blockui :blocked="editBlock">
            <p-panel>
                <template #header>
                    <i class="pi pi-cog mr-2" style="font-size: 2rem"></i>
                    <b>{{RKW_Web.Add}}</b>
                </template>
                <div class="grid">
                    <div class="col-3">
                        <div class="p-inputgroup">
                            <span class="p-inputgroup-addon">
                                <i class="pi pi-wrench"></i>
                            </span>
                            <p-dropdown v-model="item.label" :options="itemSet" option-label="label" 
                            option-value="value" placeholder="规格"></p-dropdown>
                        </div>
                    </div>
                    <div class="col-7">
                        <div class="p-inputgroup">
                            <span class="p-inputgroup-addon">
                                <i class="pi pi-info"></i>
                            </span>
                            <p-chips v-model="item.value" :placeholder="RKW_Web.BookDetailEditSpecValue" separator=","></p-chips>
                        </div>
                    </div>
                    <div class="col-2">
                        <p-button :label="RKW_Web.Add" icon="pi pi-save"
                                  @click="addItem"></p-button>
                    </div>
                </div>
            </p-panel>
            <p-panel>
                <template #header>
                    <i class="pi pi-cog mr-2" style="font-size: 2rem"></i>
                    <b>{{RKW_Web.Edit}}</b>
                </template>
                <div v-if="tmpItems.length != 0">
                    <p-datatable dataKey="id" :value="tmpItems" responsive-layout="scroll"
                                 class="p-datatable-sm" striped-rows @row-reorder="onRowReorder"
                                 context-menu v-model:context-menu-selection="selectedItem"
                                 @row-contextmenu="rowMenu" edit-mode="row"
                                 v-model:editing-rows="editingRows" @row-edit-save="onRowEditSave">
                        <p-column :row-reorder="true"></p-column>
                        <p-column field="label" :header="RKW_Web.BookDetailEditSpecLabel">
                            <template #body="slotProps">
                                {{value2Label(slotProps.data.label, itemSet)}}
                            </template>
                            <template #editor="{ data, field }">
                                <p-dropdown v-model="data[field]" :options="itemSet" option-label="label" 
                                            option-value="value" :placeholder="RKW_Web.BookDetailEditSpecLabel"></p-dropdown>
                            </template>
                        </p-column>
                        <p-column field="value" :header="RKW_Web.BookDetailEditSpecValue">
                            <template #body="slotProps">
                                {{slotProps.data.value.join(", ")}}
                            </template>
                            <template #editor="{ data, field }">
                                <p-chips v-model="data[field]"></p-chips>
                            </template>
                        </p-column>
                        <p-column :row-editor="true" style="width:10%; min-width:8rem"
                                  bodyStyle="text-align:center"></p-column>
                    </p-datatable>
                    <p-contextmenu :model="menuModel" ref="cm"></p-contextmenu>
                </div>
                <div v-else>
                    <span class="emptyInfo">{{RKW_Web.ItemDetailMessageNoSpec}}</span>
                </div>
            </p-panel>
            <div class="text-end mt-3 mb-2">
                <p-button :label="RKW_Web.Clear" icon="pi pi-trash" class="p-button-danger mr-4"
                          @click="clear" :disabled="editBlock"></p-button>
                <p-button :label="RKW_Web.Cancel" icon="pi pi-times" class="mr-4"
                          @click="cancelEdit" :disabled="editBlock"></p-button>
                <p-button :label="RKW_Web.Update" icon="pi pi-save" class="p-button-success mr-4"
                          @click="submit" :disabled="editBlock"></p-button>
            </div>
        </p-blockui>
    `,
    inject: ['dialogRef'],
    data() {
        return {
            toast: null,
            entityId: null,
            entityType: null,
            editBlock: false,
            itemSet: [],

            tmpItems: [],
            selectedItem: null,
            item: {},
            items: [],
            menuModel: [{label: RKW_Web.Delete, icon: 'pi pi-fw pi-trash', command: () => this.deleteItem(this.selectedItem)}],
            editingRows: [],
            RKW_Web,
        }
    },
    mounted() {
        this.init();
        this.entityId = this.dialogRef.data.entityId;
        this.entityType = this.dialogRef.data.entityType;
        this.toast = this.dialogRef.data.toast;
        this.itemSet = this.dialogRef.data.itemSet;

        this.tmpItems = JSON.parse(JSON.stringify(this.dialogRef.data.items));
    },
    watch: {

    },
    methods: {
        init() {

        },
        onRowReorder(ev) {
            this.tmpItems = ev.value;
        },
        rowMenu(ev) {
            this.$refs.cm.show(ev.originalEvent);
        },
        deleteItem(item) {
            this.tmpItems = this.tmpItems.filter((i) => i.label !== item.label);
            this.toast.add({severity: 'error', summary: RKW_Web.MessageDeleted, detail: item.value, life: 3000});
            this.selectedItem = null;
        },
        onRowEditSave(ev) {
            let {newData, index} = ev;
            this.tmpItems[index] = newData;
        },
        clear() {
            this.tmpItems = [];
        },
        addItem() {
            this.tmpItems.push(this.item);
            this.item = {};
        },
        cancelEdit() {
            this.item = {};
            this.dialogRef.close();
        },

        submit() {
            this.editBlock = true;
            let json = {
                entityType: this.entityType,
                entityId: this.entityId,
                spec: this.tmpItems
            }
            HttpUtil.commonVueSubmit(this.toast, UPDATE_SPECS_URL, json)
                .then(res => {
                    if (res.state === 1) {
                        this.dialogRef.close();
                        this.item = {};
                        location.reload(true);
                    }else {
                        this.editBlock = false;
                    }
                });
        },
        value2Label
    },
    components: {
        "p-button": primevue.button,
        "p-blockui": primevue.blockui,
        "p-datatable": primevue.datatable,
        "p-column": primevue.column,
        "p-panel": primevue.panel,
        "p-chips": primevue.chips,
        "p-inputtext": primevue.inputtext,
        "p-contextmenu": primevue.contextmenu,
        "p-dropdown": primevue.dropdown,
    }
};

const companiesEditPanel = {
    template: `
        <p-blockui :blocked="editBlock">
            <p-panel>
                <template #header>
                    <i class="pi pi-building mr-2" style="font-size: 2rem"></i>
                    <b>{{RKW_Web.Add}}</b>
                </template>
                <div class="grid">
                    <div class="col-3">
                        <div class="p-inputgroup">
                            <span class="p-inputgroup-addon">
                                <i class="pi pi-building"></i>
                            </span>
                            <p-dropdown v-model="company.role" :options="companyRoleSet" option-label="label" 
                            option-value="value" placeholder="企业类型"></p-dropdown>
                        </div>
                    </div>
                    <div class="col-7">
                        <div class="p-inputgroup">
                            <span class="p-inputgroup-addon">
                                <i class="pi pi-building"></i>
                            </span>
                            <p-multiselect v-model="company.members" :options="companySet"
                                    option-label="label" option-value="value" placeholder="企业"
                                    display="chip" :filter="true">
                            </p-multiselect>
                        </div>
                    </div>
                    <div class="col-2">
                        <p-button :label="RKW_Web.Add" icon="pi pi-save"
                                    @click="add"></p-button>
                    </div>
                </div>
            </p-panel>
            <p-panel>
                <template #header>
                    <i class="pi pi-user-edit mr-2" style="font-size: 2rem"></i>
                    <b>{{RKW_Web.Edit}}</b>
                </template>
                <div v-if="tmpCompanies.length != 0">
                    <p-datatable dataKey="id" :value="tmpCompanies" responsive-layout="scroll"
                                    class="p-datatable-sm" striped-rows @row-reorder="companyOnRowReorder"
                                    context-menu v-model:context-menu-selection="selectedCompany"
                                    @row-contextmenu="companyRowMenu" edit-mode="row"
                                    v-model:editing-rows="companyEditingRows" @row-edit-save="companyOnRowEditSave">
                        <p-column :row-reorder="true"></p-column>
                        <p-column field="role" header="类型">
                            <template #body="slotProps">
                                {{value2Label(slotProps.data.role, companyRoleSet)}}
                            </template>
                            <template #editor="{ data, field }">
                                <p-dropdown v-model="data[field]" :options="companyRoleSet" option-label="label" 
                                            option-value="value" placeholder="企业类型"></p-dropdown>
                            </template>
                        </p-column>
                        <p-column field="members" header="关联企业">
                            <template #body="slotProps">
                                {{commonValuesToLabels(slotProps.data.members, companySet).join(", ")}}
                            </template>
                            <template #editor="{ data, field }">
                                <p-multiselect v-model="data[field]" :options="companySet"
                                    option-label="label" option-value="value" placeholder="企业"
                                    display="chip" :filter="true">
                                </p-multiselect>
                            </template>
                        </p-column>
                        <p-column :row-editor="true" style="width:10%; min-width:8rem"
                                    bodyStyle="text-align:center"></p-column>
                    </p-datatable>
                    <p-contextmenu :model="companyMenuModel" ref="companyCm"></p-contextmenu>
                </div>
                <div v-else>
                    <span class="emptyInfo">暂无关联企业</span>
                </div>
            </p-panel>
            <div class="text-end mt-3 mb-2">
                <p-button :label="RKW_Web.Clear" icon="pi pi-trash" class="p-button-danger mr-4"
                            @click="clear" :disabled="editBlock"></p-button>
                <p-button :label="RKW_Web.Cancel" icon="pi pi-times" class="mr-4"
                            @click="cancelEdit" :disabled="editBlock"></p-button>
                <p-button :label="RKW_Web.Update" icon="pi pi-save" class="p-button-success mr-4"
                            @click="submit" :disabled="editBlock"></p-button>
            </div>
        </p-blockui>
    `,
    inject: ['dialogRef'],
    data() {
        return {
            toast: null,
            entityId: null,
            entityType: null,
            companies: [],
            editBlock: false,

            companySet: [],
            companyRoleSet: [],

            selectedCompany: null,
            company: {},
            tmpCompanies: [],
            companyMenuModel: [{label: RKW_Web.Delete, icon: 'pi pi-fw pi-user-minus', command: () => this.deleteCompany(this.selectedCompany)}],
            companyEditingRows: [],
            RKW_Web,
        }
    },
    mounted() {
        this.entityId = this.dialogRef.data.entityId;
        this.entityType = this.dialogRef.data.entityType;
        this.companies = this.dialogRef.data.companies;
        this.companyRoleSet = this.dialogRef.data.companyRoleSet;
        this.companySet = this.dialogRef.data.companySet;
        this.toast = this.dialogRef.data.toast;

        this.tmpCompanies = JSON.parse(JSON.stringify(this.companies));
    },
    watch: {

    },
    methods: {
        companyOnRowReorder(ev) {
            this.tmpCompanies.value = ev.value;
        },
        companyRowMenu(ev) {
            this.$refs.companyCm.show(ev.originalEvent);
        },
        deleteCompany(company) {
            this.tmpCompanies = this.tmpCompanies.filter((c) => c.role !== company.role);
            this.toast.add({severity: 'error', summary: RKW_Web.MessageDeleted, detail: company.role, life: 3000});
            this.selectedCompany = null;
        },
        companyOnRowEditSave(ev) {
            let {newData, index} = ev;
            this.tmpCompanies[index] = newData;
        },
        clear() {
            this.tmpCompanies = [];
        },
        add() {
            if(this.company.role === undefined || this.company.role === null) {
                this.toast.add({severity: 'error', summary: '', detail: RKW_Web.companyRoleEmpty, life: 3000});
                return;
            }else if (this.company.members === undefined || this.company.members === null || this.company.members.length === 0) {
                this.toast.add({severity: 'error', summary: '', detail: RKW_Web.companyMemberEmpty, life: 3000});
                return;
            }
            this.tmpCompanies.push(this.company);
            this.company = {};
        },
        cancelEdit() {
            this.company = {};
            this.dialogRef.close();
        },

        submit() {
            this.editBlock = true;
            let json = {
                entityType: this.entityType,
                entityId: this.entityId,
                companies: this.tmpCompanies
            }
            HttpUtil.commonVueSubmit(this.toast, UPDATE_COMPANIES_URL, json)
                .then(res => {
                    if (res.state === 1) {
                        this.dialogRef.close();
                        this.company = {};
                        location.reload(true);
                    }else {
                        this.editBlock = false;
                    }
                });
        },
        commonValuesToLabels,
        value2Label
    },
    components: {
        "p-button": primevue.button,
        "p-blockui": primevue.blockui,
        "p-datatable": primevue.datatable,
        "p-column": primevue.column,
        "p-panel": primevue.panel,
        "p-dropdown": primevue.dropdown,
        "p-multiselect": primevue.multiselect,
        "p-contextmenu": primevue.contextmenu,
    }
};

const personnelEditPanel = {
    template: `
        <p-blockui :blocked="editBlock">
    <p-panel>
        <template #header>
            <i class="pi pi-user-plus mr-2" style="font-size: 2rem"></i>
            <b>{{RKW_Web.Add}}</b>
        </template>
        <div class="grid">
            <div class="col-3">
                <div class="p-inputgroup">
                    <span class="p-inputgroup-addon">
                        <i class="pi pi-tag"></i>
                    </span>
                    <span class="p-inputgroup-addon">
                        <p-checkbox v-model="item.main" :binary="true" :true-value="1" :false-value="0"></p-checkbox>
                    </span>
                    <p-dropdown v-model="item.role" :options="roleSet" option-label="label" filter 
                    option-value="value" placeholder="角色"></p-dropdown>
                </div>
            </div>
            <div class="col-7">
                <div class="p-inputgroup">
                    <span class="p-inputgroup-addon">
                        <i class="pi pi-users"></i>
                    </span>
                    <p-autocomplete v-model="item.members" :multiple="true" :delay="500"
                    option-label="label" :suggestions="querySet" @complete="search">
                        <template #option="slotProps">
                            <div class="flex align-options-center">
                                <img :alt="slotProps.option.label" src="/img/tmp/13.jpg" style="width: 18px" />
                                <div>{{ slotProps.option.label }}</div>
                            </div>
                        </template>
                    </p-autocomplete>
<!--                    <p-multiselect v-model="item.members" :options="personnelSet"-->
<!--                            option-label="label" option-value="value" placeholder="成员"-->
<!--                            display="chip" :filter="true">-->
<!--                    </p-multiselect>-->
                </div>
            </div>
            <div class="col-2">
                <p-button :label="RKW_Web.Add" icon="pi pi-save"
                            @click="add"></p-button>
            </div>
        </div>
    </p-panel>
    <p-panel>
        <template #header>
            <i class="pi pi-user-edit mr-2" style="font-size: 2rem"></i>
            <b>{{RKW_Web.Edit}}</b>
        </template>
        <div v-if="tmpItems.length != 0">
            <p-datatable dataKey="id" :value="tmpItems" responsive-layout="scroll"
                            class="p-datatable-sm" striped-rows @row-reorder="onRowReorder"
                            context-menu v-model:context-menu-selection="selectedItem"
                            @row-contextmenu="rowMenu" edit-mode="row"
                            v-model:editing-rows="editingRows" @row-edit-save="onRowEditSave">
                <p-column :row-reorder="true"></p-column>
                <p-column field="main" header-style="width: 3%">
                    <template #body="slotProps">
                        <i v-if="slotProps.data.main == 1" class="pi false-icon pi-star-fill"></i>
                    </template>
                    <template #editor="{ data, field }">
                        <p-checkbox v-model="data[field]" :binary="true" :true-value="1" :false-value="0"></p-checkbox>
                    </template>
                </p-column>
                <p-column field="role" header="角色">
                    <template #body="slotProps">
                        {{value2Label(slotProps.data.role, roleSet)}}
                    </template>
                    <template #editor="{ data, field }">
                        <p-dropdown v-model="data[field]" :options="roleSet" option-label="label" filter 
                                    option-value="value" placeholder="角色"></p-dropdown>
                    </template>
                </p-column>
                <p-column field="members" header="成员">
                    <template #body="slotProps">
                        {{commonValuesToLabels(slotProps.data.members, personnelSet).join(", ")}}
                    </template>
                    <template #editor="{ data, field }">
                        <p-autocomplete v-model="data[field]" :multiple="true" :delay="500"
                        option-label="label" :suggestions="querySet" @complete="search">
                            <template #option="slotProps">
                                <div class="flex align-options-center">
                                    <img :alt="slotProps.option.label" src="/img/tmp/13.jpg" style="width: 18px" />
                                    <div>{{ slotProps.option.label }}</div>
                                </div>
                            </template>
                        </p-autocomplete>
<!--                        <p-multiselect v-model="data[field]" :options="personnelSet"-->
<!--                            option-label="label" option-value="value" placeholder="成员"-->
<!--                            display="chip" :filter="true">-->
<!--                        </p-multiselect>-->
                    </template>
                </p-column>
                <p-column :row-editor="true" style="width:10%; min-width:8rem"
                            bodyStyle="text-align:center"></p-column>
            </p-datatable>
            <p-contextmenu :model="menuModel" ref="cm"></p-contextmenu>
        </div>
        <div v-else>
            <span class="emptyInfo">暂无关联人员信息</span>
        </div>
    </p-panel>
    <div class="text-end mt-3 mb-2">
        <p-button :label="RKW_Web.Clear" icon="pi pi-trash" class="p-button-danger mr-4"
                    @click="clear" :disabled="editBlock"></p-button>
        <p-button :label="RKW_Web.Cancel" icon="pi pi-times" class="mr-4"
                    @click="cancelEdit" :disabled="editBlock"></p-button>
        <p-button :label="RKW_Web.Update" icon="pi pi-save" class="p-button-success mr-4"
                    @click="submit" :disabled="editBlock"></p-button>
    </div>
</p-blockui>
    `,
    inject: ['dialogRef'],
    data() {
        return {
            toast: null,
            entityId: null,
            entityType: null,
            items: [],
            editBlock: false,

            personnelSet: [],
            roleSet: [],

            selectedItem: null,
            item: {},
            tmpItems: [],
            menuModel: [{label: RKW_Web.Delete, icon: 'pi pi-fw pi-user-minus', command: () => this.deleteItem(this.selectedItem)}],
            editingRows: [],
            querySet: [],
            RKW_Web,
        }
    },
    mounted() {
        this.entityId = this.dialogRef.data.entityId;
        this.entityType = this.dialogRef.data.entityType;
        this.items = this.dialogRef.data.items;
        this.roleSet = this.dialogRef.data.roleSet;
        this.personnelSet = this.dialogRef.data.personnelSet;
        this.toast = this.dialogRef.data.toast;

        this.tmpItems = JSON.parse(JSON.stringify(this.items));
    },
    watch: {

    },
    methods: {
        search(ev) {
            setTimeout(() => {
                if (!ev.query.trim().length) {
                    this.querySet = [...this.personnelSet];
                } else {
                    this.querySet = this.personnelSet.filter((personnel) => {
                        return personnel.label.toLowerCase().startsWith(ev.query.toLowerCase());
                    });
                }
                console.log(this.item.members);
            }, 300)
        },
        onRowReorder(ev) {
            this.tmpItems = ev.value;
        },
        rowMenu(ev) {
            this.$refs.cm.show(ev.originalEvent);
        },
        deleteItem(item) {
            this.tmpItems = this.tmpItems.filter((i) => i.role !== item.role);
            this.toast.add({severity: 'error', summary: RKW_Web.MessageDeleted, detail: item.role, life: 3000});
            this.selectedItem = null;
        },
        onRowEditSave(ev) {
            let {newData, index} = ev;
            this.tmpItems[index] = newData;
        },
        clear() {
            this.tmpItems = [];
        },
        add() {
            if(this.item.role === undefined || this.item.role === null) {
                this.toast.add({severity: 'error', summary: '', detail: RKW_Web.personnelRoleEmpty, life: 3000});
                return;
            }else if (this.item.members === undefined || this.item.members === null || this.item.members.length === 0) {
                this.toast.add({severity: 'error', summary: '', detail: RKW_Web.personnelMemberEmpty, life: 3000});
                return;
            }
            this.tmpItems.push(this.item);
            this.item = {};
        },
        cancelEdit() {
            this.item = {};
            this.dialogRef.close();
        },

        submit() {
            this.editBlock = true;
            let mainCount = 0;
            this.tmpItems.forEach(item => {
                if(item.main === 1) {
                    mainCount++;
                }
            });
            if(mainCount > 1) {
                this.toast.add({severity: 'error', summary: RKW_Web.ErrorMessageItemDetailPersonnelMain, life: 3000});
                editBlock.value = false;
                return;
            }
            let json = {
                entityType: this.entityType,
                entityId: this.entityId,
                personnel: this.tmpItems
            }
            HttpUtil.commonVueSubmit(this.toast, UPDATE_PERSONNEL_URL, json)
                .then(res => {
                    if (res.state === 1) {
                        this.dialogRef.close();
                        this.item = {};
                        location.reload(true);
                    }else {
                        this.editBlock = false;
                    }
                });
        },
        commonValuesToLabels,
        value2Label
    },
    components: {
        "p-button": primevue.button,
        "p-blockui": primevue.blockui,
        "p-datatable": primevue.datatable,
        "p-column": primevue.column,
        "p-panel": primevue.panel,
        "p-dropdown": primevue.dropdown,
        "p-multiselect": primevue.multiselect,
        "p-contextmenu": primevue.contextmenu,
        "p-checkbox": primevue.checkbox,
        "p-autocomplete": primevue.autocomplete,
    }
};