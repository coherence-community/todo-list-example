/**
 * @license
 * Copyright (c) 2014, 2020, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */

"use strict";

define(['ojs/ojcore', 'jquery', 'ojs/ojeventtarget', 'ojs/ojdataprovider'], function(oj, $)
{
class PagingDataProviderView {
    constructor(dataProvider) {
        this.dataProvider = dataProvider;
        this._KEY = 'key';
        this._KEYS = 'keys';
        this._STARTINDEX = 'startIndex';
        this._PAGESIZE = 'pageSize';
        this._OFFSET = 'offset';
        this._SIZE = 'size';
        this._PAGE = 'page';
        this._PAGECOUNT = 'pageCount';
        this._TOTALSIZE = 'totalsize';
        this._PREVIOUSPAGE = 'previousPage';
        this._BEFOREPAGE = 'beforePage';
        this._DONE = 'done';
        this._VALUE = 'value';
        this._DATA = 'data';
        this._REFRESH = 'refresh';
        this._MUTATE = 'mutate';
        this._SORTCRITERIA = 'sortCriteria';
        this._FILTERCRITERION = 'filterCriterion';
        this._METADATA = 'metadata';
        this._RESULTS = 'results';
        this._FETCHPARAMETERS = 'fetchParameters';
        this._CONTAINSPARAMETERS = 'containsParameters';
        this._CONTAINSKEYS = 'containsKeys';
        this._FETCHBYKEYS = 'fetchByKeys';
        this._FETCHBYOFFSET = 'fetchByOffset';
        this._AFTERKEYS = 'afterKeys';
        this._ADDBEFOREKEYS = 'addBeforeKeys';
        this._ADD = 'add';
        this._REMOVE = 'remove';
        this._UPDATE = 'update';
        this._INDEXES = 'indexes';
        this.AsyncIterable = class {
            constructor(_parent, _asyncIterator) {
                this._parent = _parent;
                this._asyncIterator = _asyncIterator;
                this[Symbol.asyncIterator] = function () {
                    return this._asyncIterator;
                };
            }
        };
        this.AsyncIterator = class {
            constructor(_parent, _nextFunc, _params, _clientId) {
                this._parent = _parent;
                this._nextFunc = _nextFunc;
                this._params = _params;
                this._clientId = _clientId;
            }
            ['next']() {
                let result = this._nextFunc(this._params, this._clientId);
                return Promise.resolve(result);
            }
        };
        this.AsyncIteratorYieldResult = class {
            constructor(_parent, value) {
                this._parent = _parent;
                this.value = value;
                this[_parent._VALUE] = value;
                this[_parent._DONE] = false;
            }
        };
        this.AsyncIteratorReturnResult = class {
            constructor(_parent, value) {
                this._parent = _parent;
                this.value = value;
                this[_parent._VALUE] = value;
                this[_parent._DONE] = true;
            }
        };
        this.FetchListParameters = class {
            constructor(_parent, size, sortCriteria, filterCriterion) {
                this._parent = _parent;
                this.size = size;
                this.sortCriteria = sortCriteria;
                this.filterCriterion = filterCriterion;
                this[_parent._SIZE] = size;
                this[_parent._SORTCRITERIA] = sortCriteria;
                this[_parent._FILTERCRITERION] = filterCriterion;
            }
        };
        this.FetchListResult = class {
            constructor(_parent, fetchParameters, data, metadata) {
                this._parent = _parent;
                this.fetchParameters = fetchParameters;
                this.data = data;
                this.metadata = metadata;
                this[_parent._FETCHPARAMETERS] = fetchParameters;
                this[_parent._DATA] = data;
                this[_parent._METADATA] = metadata;
            }
        };
        this.FetchByOffsetParameters = class {
            constructor(_parent, offset, size, sortCriteria, filterCriterion) {
                this._parent = _parent;
                this.offset = offset;
                this.size = size;
                this.sortCriteria = sortCriteria;
                this.filterCriterion = filterCriterion;
                this[_parent._SIZE] = size;
                this[_parent._SORTCRITERIA] = sortCriteria;
                this[_parent._OFFSET] = offset;
                this[_parent._FILTERCRITERION] = filterCriterion;
            }
        };
        this.FetchByOffsetResults = class {
            constructor(_parent, fetchParameters, results, done) {
                this._parent = _parent;
                this.fetchParameters = fetchParameters;
                this.results = results;
                this.done = done;
                this[_parent._FETCHPARAMETERS] = fetchParameters;
                this[_parent._RESULTS] = results;
                this[_parent._DONE] = done;
            }
        };
        this.FetchByKeysResults = class {
            constructor(_parent, fetchParameters, results) {
                this._parent = _parent;
                this.fetchParameters = fetchParameters;
                this.results = results;
                this[_parent._FETCHPARAMETERS] = fetchParameters;
                this[_parent._RESULTS] = results;
            }
        };
        this.ContainsKeysResults = class {
            constructor(_parent, containsParameters, results) {
                this._parent = _parent;
                this.containsParameters = containsParameters;
                this.results = results;
                this[_parent._CONTAINSPARAMETERS] = containsParameters;
                this[_parent._RESULTS] = results;
            }
        };
        this.ItemMetadata = class {
            constructor(_parent, key) {
                this._parent = _parent;
                this.key = key;
                this[_parent._KEY] = key;
            }
        };
        this.DataProviderMutationEventDetail = class {
            constructor(_parent, add, remove, update) {
                this._parent = _parent;
                this.add = add;
                this.remove = remove;
                this.update = update;
                this[_parent._ADD] = add;
                this[_parent._REMOVE] = remove;
                this[_parent._UPDATE] = update;
            }
        };
        this.DataProviderOperationEventDetail = class {
            constructor(_parent, keys, metadata, data, indexes) {
                this._parent = _parent;
                this.keys = keys;
                this.metadata = metadata;
                this.data = data;
                this.indexes = indexes;
                this[_parent._KEYS] = keys;
                this[_parent._METADATA] = metadata;
                this[_parent._DATA] = data;
                this[_parent._INDEXES] = indexes;
            }
        };
        this.DataProviderAddOperationEventDetail = class {
            constructor(_parent, keys, afterKeys, addBeforeKeys, metadata, data, indexes) {
                this._parent = _parent;
                this.keys = keys;
                this.afterKeys = afterKeys;
                this.addBeforeKeys = addBeforeKeys;
                this.metadata = metadata;
                this.data = data;
                this.indexes = indexes;
                this[_parent._KEYS] = keys;
                this[_parent._AFTERKEYS] = afterKeys;
                this[_parent._ADDBEFOREKEYS] = addBeforeKeys;
                this[_parent._METADATA] = metadata;
                this[_parent._DATA] = data;
                this[_parent._INDEXES] = indexes;
            }
        };
        let self = this;
        this._addEventListeners(dataProvider);
        this._currentPage = -1;
        this._pageSize = -1;
        this._pageCount = -1;
        this._offset = 0;
        this._mutationOffset = 0;
        this._totalSize = -1;
        this._skipCriteriaCheck = false;
        // set up initialize promise check to make sure setPage is called before
        // fetching data
        this._isInitialized = new Promise(function (resolve) {
            self._resolveFunc = resolve;
        });
        // set up initialize data promise check to make sure data is loaded
        // before View fetch calls are allowed to continue
        this._isInitialDataLoaded = new Promise(function (resolve) {
            self._dataResolveFunc = resolve;
        });
        this._hasMutated = false;
        this._selfRefresh = false;
        this._mustRefetch = false;
        this._isFetchingForMutation = false;
        this._mutationEventQueue = [];
        this._isMutating = null;
        this._mutationFunc = null;
        this._doRefreshEvent = false;
        this._mutatingTotalSize = null;
        this._fetchMore = false;
        this._isUnknownRowCount = false;
        this._fetchFirstDone = false;
        this._iteratorCacheMap = new Map();
    }
    containsKeys(params) {
        let self = this;
        return this._checkIfDataInitialized(function () {
            // if containsKeys exists, use that and filter out extra keys
            return self.dataProvider[self._CONTAINSKEYS](params).then(function (value) {
                let keys = value.results;
                if (!self._isGlobal(params)) {
                    let currentPageResults = new Set();
                    let currentPageKeys = self._getCurrentPageKeys();
                    keys.forEach(function (key) {
                        if (currentPageKeys.indexOf(key) != -1) {
                            currentPageResults.add(key);
                        }
                    });
                    return new self.ContainsKeysResults(self, params, currentPageResults);
                }
                else {
                    return new self.ContainsKeysResults(self, params, keys);
                }
            });
        });
    }
    fetchByKeys(params) {
        let self = this;
        return this._checkIfDataInitialized(function () {
            let requestedKeys = params.keys;
            if (!self._isGlobal(params)) {
                // use the cached fetch data to get values by keys.
                return self
                    ._fetchByOffset(new self.FetchByOffsetParameters(self, self._offset, self._pageSize, self._currentSortCriteria, self._currentFilterCriteria))
                    .then(function (results) {
                    let result = results['results'];
                    let mappedResultMap = new Map();
                    let filteredResults = result.map(function (value) {
                        if (requestedKeys.has(value[self._METADATA][self._KEY])) {
                            return value;
                        }
                    });
                    filteredResults.forEach(function (value) {
                        if (value) {
                            mappedResultMap.set(value[self._METADATA][self._KEY], value);
                        }
                    });
                    return new self.FetchByKeysResults(self, params, mappedResultMap);
                });
            }
            else {
                // fetching globally so need to use the dataprovider.
                if (self.dataProvider[self._FETCHBYKEYS]) {
                    return self.dataProvider[self._FETCHBYKEYS](params);
                }
                else {
                    // doesn't exist so need to throw an error
                    throw new Error('Global scope not supported for this dataprovider');
                }
            }
        });
    }
    fetchByOffset(params) {
        let self = this;
        return this._checkIfDataInitialized(function () {
            let offset = params != null ? (params[self._OFFSET] > 0 ? params[self._OFFSET] : 0) : 0;
            params = new self.FetchByOffsetParameters(self, self._offset, self._pageSize, self._currentSortCriteria, self._currentFilterCriteria);
            return self._fetchByOffset(params).then(function (results) {
                let newResult = results['results'].filter(function (value, index) {
                    return index >= offset;
                });
                return new self.FetchByOffsetResults(self, self._getLocalParams(params), newResult, results['done']);
            });
        });
    }
    fetchFirst(params) {
        let self = this;
        // set up iterator variables
        let sortCriteria = params != null ? params[self._SORTCRITERIA] : null;
        let filterCriterion = params != null ? params[self._FILTERCRITERION] : null;
        let payload = {};
        // we can force a criteria check skip if we know that we shouldn't check
        if (self._skipCriteriaCheck) {
            self._skipCriteriaCheck = false;
        }
        else {
            // check criteria. If different, reset page to 0
            if (!self._isSameCriteria(sortCriteria, filterCriterion)) {
                self._currentSortCriteria = sortCriteria;
                self._currentFilterCriteria = filterCriterion;
                self._offset = 0;
                if (self._currentPage != 0) {
                    payload[self._PREVIOUSPAGE] = self._currentPage;
                    self._currentPage = 0;
                    payload[self._PAGE] = self._currentPage;
                }
            }
        }
        let offset = self._offset;
        let size = self._pageSize;
        let clientId = (params && params.clientId) || Symbol();
        // initialize cachemap
        this._iteratorCacheMap.set(clientId, {
            offset: offset,
            size: size,
            mutationOffset: 0,
            fetchFirstDone: false,
            currentParams: self._currentParams
        });
        // this fetchFirst applies the offset properties on the this.
        return new self.AsyncIterable(self, new self.AsyncIterator(self, (function (params, clientId) {
            return function (params, clientId) {
                let iteratorData = self._iteratorCacheMap.get(clientId);
                let offset = iteratorData['offset'];
                let size = iteratorData['size'];
                let mutationOffset = iteratorData['mutationOffset'];
                let fetchFirstDone = iteratorData['fetchFirstDone'];
                let currentParams = iteratorData['currentParams'];
                let updatedParams = new self.FetchByOffsetParameters(self, offset, size, self._currentSortCriteria, self._currentFilterCriteria);
                if (mutationOffset != 0) {
                    // we have a remove event in progress triggering a fetch
                    // Since we already have the page fetched data, we can
                    // just grab the current page data and slice what we need.
                    updatedParams = currentParams;
                }
                // Datagrid may trigger the fetchFirst first, so we need to update the params again before fetching
                let needParamUpdate = false;
                if (self._isInitialDataLoaded != null) {
                    needParamUpdate = true;
                }
                return self._checkIfDataInitialized(function () {
                    if (needParamUpdate) {
                        size = self._pageSize;
                        offset = self._offset;
                        updatedParams = new self.FetchByOffsetParameters(self, offset, size, self._currentSortCriteria, self._currentFilterCriteria);
                    }
                    return self._fetchByOffset(updatedParams).then(function (result) {
                        let results = result['results'];
                        // If the fetch first done flag is true, then the last fetch should have been all the data
                        // Return no results since datagrid requires 0 results to conclude fetch
                        // Skip this if mutationOffset is non-zero, since it's an extra fetch caused by mutation
                        if (fetchFirstDone && mutationOffset == 0) {
                            results = [];
                        }
                        if (mutationOffset != 0) {
                            // Do a slice for mutation offset if applicable.
                            results = results.slice(results.length - mutationOffset);
                        }
                        let data = results.map(function (value) {
                            return value[self._DATA];
                        });
                        let metadata = results.map(function (value) {
                            return value[self._METADATA];
                        });
                        offset = offset + metadata.length - mutationOffset;
                        // fire page change event in the case of sort operation resetting the page to 0
                        if (payload[self._PAGE] != null) {
                            // Update end item index
                            self._endItemIndex = self._offset + data.length - 1;
                            self.dispatchEvent(new CustomEvent(self._PAGE, { detail: payload }));
                            payload = {};
                        }
                        // Datagrid triggers fetchfirst before setPage, so we need to clear this parameter here
                        self._skipCriteriaCheck = false;
                        let resultsParam = new self.FetchByOffsetParameters(self, result['fetchParameters']['offset'] - self._mutationOffset, self._pageSize, self._currentSortCriteria);
                        // Reset mutation offset after fetch
                        mutationOffset = 0;
                        // Set done flag for datagrid fetch handling
                        fetchFirstDone = result[self._DONE];
                        // cache the new data values
                        self._iteratorCacheMap.set(clientId, {
                            offset: offset,
                            size: size,
                            mutationOffset: mutationOffset,
                            fetchFirstDone: fetchFirstDone,
                            currentParams: currentParams
                        });
                        if (result[self._DONE]) {
                            return Promise.resolve((new self.AsyncIteratorReturnResult(self, new self.FetchListResult(self, resultsParam, data, metadata))));
                        }
                        return Promise.resolve((new self.AsyncIteratorYieldResult(self, new self.FetchListResult(self, resultsParam, data, metadata))));
                    });
                });
            };
        })(), params, clientId));
    }
    getCapability(capabilityName) {
        return this.dataProvider.getCapability(capabilityName);
    }
    getTotalSize() {
        let self = this;
        return this._checkIfInitialized(function () {
            return new Promise(function (resolve) {
                resolve(self._pageSize);
            });
        });
    }
    isEmpty() {
        return this.dataProvider.isEmpty();
    }
    // Start PagingModel APIs
    getPage() {
        return this._currentPage;
    }
    setPage(value, options) {
        let self = this;
        // make sure mutation events are complete before starting
        return this._mutationBusyContext(function () {
            value = parseInt(value, 10);
            let payload = {};
            payload[self._PAGE] = value;
            payload[self._PREVIOUSPAGE] = self._currentPage;
            self.dispatchEvent(new CustomEvent(self._BEFOREPAGE, { detail: payload }));
            if (options[self._PAGESIZE] != null) {
                self._pageSize = options[self._PAGESIZE];
            }
            self._offset = parseInt(value, 10) * self._pageSize;
            self._currentPage = value;
            // Handle initialization confirmation
            if (self._isInitialized != null) {
                self._resolveFunc(true);
                self._updateTotalSize();
            }
            let params = new self.FetchByOffsetParameters(self, self._offset, self._pageSize, self._currentSortCriteria, self._currentFilterCriteria);
            return self._fetchByOffset(params).then(function (results) {
                let data = results['results'];
                if (data.length !== 0) {
                    self._endItemIndex = self._offset + data.length - 1;
                    self._skipCriteriaCheck = true;
                    self.dispatchEvent(new CustomEvent(self._PAGE, { detail: payload }));
                    self._updateTotalSize();
                }
                else if (self._currentPage !== 0) {
                    // reset to previous page if no data and not on page 1
                    // since it means we were done last page
                    self._currentPage = payload[self._PREVIOUSPAGE];
                    self._offset = self._currentPage * self._pageSize;
                    self.dispatchEvent(new CustomEvent(self._PAGECOUNT, { detail: { previousValue: value, value: value } }));
                    // skip refresh
                    self._doRefreshEvent = false;
                }
                else {
                    // no data and page 0 means empty data set
                    self._offset = 0;
                    self._endItemIndex = 0;
                }
                // skip initial refresh so that view doesn't
                // double fetch. Also confirm that initial data has been loaded
                if (self._doRefreshEvent) {
                    self._hasMutated = true;
                    self._selfRefresh = true;
                    self.dispatchEvent(new oj.DataProviderRefreshEvent());
                }
                else {
                    self._dataResolveFunc(true);
                    self._doRefreshEvent = true;
                }
                if (self._isInitialDataLoaded) {
                    self._dataResolveFunc(true);
                }
            });
        });
    }
    getStartItemIndex() {
        return this._offset;
    }
    getEndItemIndex() {
        return this._endItemIndex;
    }
    getPageCount() {
        return this._pageCount;
    }
    totalSize() {
        return this._totalSize;
    }
    totalSizeConfidence() {
        if (this._totalSizeConfidence) {
            return this._totalSizeConfidence;
        }
        else {
            if (this._totalSize === -1) {
                return 'unknown';
            }
            return 'actual';
        }
    }
    // End PagingModel APIs
    // Start Paging DataProvider View Helper API
    // helper method for the view to get the global offset
    getGlobalIndex(value) {
        return this._offset + value;
    }
    // helper method for the view to get the local offset
    getLocalIndex(value) {
        return value - this._offset;
    }
    // End Paging DataProvider View Helper API
    // helper method to get local params
    _getLocalParams(params) {
        return new this.FetchByOffsetParameters(this, this.getLocalIndex(params.offset), params.size, params.sortCriteria, params.filterCriterion);
    }
    // helper method to update total size and fire events if necessary
    _updateTotalSize() {
        let self = this;
        // fire pagecount/totalsize change events if applicable
        let previousTotalSize = self._totalSize;
        let previousPageCount = self._pageCount;
        return this._checkIfInitialized(function () {
            return self.dataProvider.getTotalSize().then(function (totalSize) {
                self._totalSize = totalSize;
                // Reset page count;
                self._pageCount = -1;
                if (self._totalSize !== -1) {
                    if (self._isUnknownRowCount) {
                        // unknown row count flag means that we should be in
                        // partial row count mode now
                        self._isUnknownRowCount = false;
                        self._totalSizeConfidence = 'atLeast';
                    }
                    self._pageCount = Math.ceil(self._totalSize / self._pageSize);
                    // update offsets and currentpage if needed
                    if (self._offset >= self._totalSize) {
                        self._offset = self._totalSize - (self._totalSize % self._pageSize);
                        self._endItemIndex = self._totalSize - 1;
                        let newPage = Math.floor(self._totalSize / self._pageSize);
                        if (self._currentPage != newPage) {
                            let payload = {};
                            payload[self._PAGE] = newPage;
                            payload[self._PREVIOUSPAGE] = self._currentPage;
                            self.dispatchEvent(new CustomEvent(self._PAGE, { detail: payload }));
                            self._currentPage = newPage;
                        }
                    }
                    if (previousPageCount != self._pageCount) {
                        self.dispatchEvent(new CustomEvent(self._PAGECOUNT, {
                            detail: { previousValue: previousPageCount, value: self._pageCount }
                        }));
                    }
                    else if (previousTotalSize != self._totalSize) {
                        self.dispatchEvent(new CustomEvent(self._TOTALSIZE, {
                            detail: { previousValue: previousTotalSize, value: self._totalSize }
                        }));
                    }
                }
                return self._pageSize;
            });
        });
    }
    // busy context for mutation event handling.
    // should block setPage calls until done
    _mutationBusyContext(callback) {
        let self = this;
        if (this._isMutating) {
            return self._isMutating.then(function () {
                self._isMutating = null;
                return callback();
            });
        }
        else {
            return callback();
        }
    }
    // setup the mutation busyContext
    _setupMutationBusyContext() {
        let self = this;
        this._isMutating = new Promise(function (resolve) {
            self._mutationFunc = resolve;
        });
    }
    // helper method to check if paging control dataprovider view is initialized with currentpage
    _checkIfInitialized(callback) {
        let self = this;
        if (this._isInitialized) {
            return self._isInitialized.then(function (value) {
                // make sure currentPage is set
                if (!value || self._currentPage == -1) {
                    self._isInitialized = null;
                    throw new Error('Paging DataProvider View incorrectly initialized');
                }
                else {
                    self._isInitialized = null;
                    return callback();
                }
            });
        }
        else {
            return callback();
        }
    }
    // helper method to check if paging control dataprovider view is initialized with data
    _checkIfDataInitialized(callback) {
        let self = this;
        if (this._isInitialDataLoaded) {
            return self._isInitialDataLoaded.then(function (value) {
                // make sure currentPage is set
                if (!value || self._currentPage == -1) {
                    self._isInitialDataLoaded = null;
                    throw new Error('Paging DataProvider View incorrectly initialized');
                }
                else {
                    self._isInitialDataLoaded = null;
                    return callback();
                }
            });
        }
        else {
            return callback();
        }
    }
    // helper method to determine if a row is in the current page.
    _getCurrentPageKeys() {
        let self = this;
        let currentPageKeys = this._currentResults.map(function (value) {
            return value[self._METADATA][self._KEY];
        });
        return currentPageKeys;
    }
    // helper method to check that all params for the current fetched data are still the same
    _isSameParams(params) {
        if (this._currentParams[this._SIZE] === params[this._SIZE] &&
            this._currentParams[this._OFFSET] === params[this._OFFSET] &&
            this._currentParams[this._SORTCRITERIA] === params[this._SORTCRITERIA] &&
            this._currentParams[this._FILTERCRITERION] === params[this._FILTERCRITERION]) {
            return true;
        }
        else {
            return false;
        }
    }
    // helper method to determine if the criteria is the same
    _isSameCriteria(sortCriteria, filterCriterion) {
        if (sortCriteria) {
            if (!this._currentSortCriteria ||
                sortCriteria[0]['attribute'] != this._currentSortCriteria[0]['attribute'] ||
                sortCriteria[0]['direction'] != this._currentSortCriteria[0]['direction']) {
                return false;
            }
        }
        else {
            if (this._currentSortCriteria) {
                return false;
            }
        }
        if (filterCriterion) {
            if (!this._currentFilterCriteria) {
                return false;
            }
            else {
                // need to do deep filter compare from both sides
                for (const prop in this._currentFilterCriteria) {
                    if (!this._filterCompare(this._currentFilterCriteria, filterCriterion, prop)) {
                        return false;
                    }
                }
                for (const prop in filterCriterion) {
                    if (!this._filterCompare(this._currentFilterCriteria, filterCriterion, prop)) {
                        return false;
                    }
                }
            }
        }
        else {
            if (this._currentFilterCriteria) {
                return false;
            }
        }
        return true;
    }
    _filterCompare(cachedFilter, newFilter, prop) {
        if (cachedFilter[prop] && newFilter[prop] && cachedFilter[prop] == newFilter[prop]) {
            return true;
        }
        return false;
    }
    _isGlobal(params) {
        return params.scope != undefined && params.scope === 'global';
    }
    // helper method to get current page data
    _getCurrentPageData() {
        let self = this;
        // if params haven't changed, just return what we already have
        // Also need to check that offset and page size are properly set
        if (self._currentParams &&
            self._currentParams['offset'] === self._offset &&
            self._currentParams['size'] === self._pageSize) {
            if (self._currentResults && !self._hasMutated) {
                return new Promise(function (resolve) {
                    resolve(new self.FetchByOffsetResults(self, self._getLocalParams(self._currentParams), self._currentResults, self._currentIsDone));
                });
            }
            else {
                return self._fetchByOffset(self._currentParams).then(function (result) {
                    return result;
                });
            }
        }
        else {
            return self
                ._fetchByOffset(new self.FetchByOffsetParameters(self, self._offset, self._pageSize, self._currentSortCriteria, self._currentFilterCriteria))
                .then(function (result) {
                return result;
            });
        }
    }
    // internal fetch by offset method to get page data based on offset
    _fetchByOffset(params) {
        let self = this;
        return this._checkIfInitialized(function () {
            // if params haven't changed, just return what we already have
            if (self._currentParams &&
                self._isSameParams(params) &&
                (!self._hasMutated || self._selfRefresh)) {
                self._selfRefresh = false;
                self._hasMutated = false;
                return new Promise(function (resolve) {
                    resolve(new self.FetchByOffsetResults(self, self._getLocalParams(self._currentParams), self._currentResults, self._currentIsDone));
                });
            }
            params = self._cleanFetchParams(params);
            // If size is 0, then we have hit the end of the data and we should be
            // returning empty results with done = true
            if (params.size === 0) {
                self._currentIsDone = true;
                self._currentResults = [];
                self._currentParams = params;
                return new Promise(function (resolve) {
                    resolve(new self.FetchByOffsetResults(self, self._getLocalParams(params), [], self._currentIsDone));
                });
            }
            return self._fetchByOffsetHelper(params);
        });
    }
    _fetchByOffsetHelper(params) {
        let self = this;
        // perform a fetch by offset using the available params.
        return self.dataProvider[self._FETCHBYOFFSET](params)
            .then(function (result) {
            // store results locally. If fetchMore is true, we should append instead of replace
            self._currentIsDone = result['done'];
            if (self._fetchMore) {
                self._currentResults = self._currentResults.concat(result['results']);
            }
            else {
                self._currentResults = result['results'];
                self._currentParams = params;
            }
            self._fetchMore = false;
            // check if we are at the end of the available data
            let resultSize = self._currentResults.length;
            let newSize = self._offset + resultSize;
            if (result['done']) {
                self._pageCount = Math.ceil(newSize / self._pageSize);
                // if partial, can change back to exact.
                if (self._totalSizeConfidence) {
                    self._totalSizeConfidence = null;
                }
            }
            else if (!result['done'] &&
                newSize >= self._totalSize &&
                self._totalSize > -1 &&
                params.size === self._pageSize) {
                // we have more data than we expect given the totalSize, so we should be in partial mode
                self._totalSizeConfidence = 'atLeast';
                self._pageCount = self._pageCount + 1;
            }
            else if (!result['done'] && resultSize < self._pageSize) {
                // we are not done and we have less data than we expected.
                // need to refetch with new params since the underlying dataprovider
                // fetchSize is smaller than pageSize
                self._fetchMore = true;
                let newParams = new self.FetchByOffsetParameters(self, newSize, self._pageSize - resultSize, self._currentSortCriteria, self._currentFilterCriteria);
                return self._fetchByOffsetHelper(newParams);
            }
            else if (!result['done'] && self._totalSize === -1) {
                // either we haven't initialized total size yet,
                // or we are in unknown row count mode
                self._isUnknownRowCount = true;
            }
            // check if pageSize matches length or if length and offset hits total size.
            if (self._pageSize == self._currentResults.length ||
                (newSize >= self._totalSize && self._totalSize > -1)) {
                self._currentIsDone = true;
            }
            // Truncate results down to pageSize if needed to prevent extra data
            // bug JET-33250
            if (resultSize > self._pageSize) {
                self._currentResults.splice(self._pageSize);
            }
            // updated data so set mutated flag to false;
            self._hasMutated = false;
            return new self.FetchByOffsetResults(self, self._getLocalParams(self._currentParams), self._currentResults, self._currentIsDone);
        })
            .catch(function (reject) {
            // if fetch is rejected, set all current records to null and reject.
            self._hasMutated = false;
            self._fetchMore = false;
            self._currentIsDone = null;
            self._currentResults = null;
            self._currentParams = null;
            return Promise.reject(reject);
        });
    }
    // Sanity Check helper method to correct any issues with the params for fetch by offset.
    _cleanFetchParams(params) {
        let self = this;
        // Sanity Check that the offset is within acceptable range
        let newOffset = params.offset;
        if (newOffset >= self._offset + self._pageSize || newOffset < self._offset) {
            newOffset = self._offset;
        }
        // Sanity Check that the pageSize is positive
        let newSize = params.size;
        if (newSize <= 0) {
            newSize = self._pageSize;
        }
        // Sanity Check that the size and offset are both within the context of the current Page limits.
        if (newOffset + newSize > self._offset + self._pageSize) {
            newSize = self._offset + self._pageSize - newOffset;
        }
        let totalSize = self._mutatingTotalSize === null ? self._totalSize : self._mutatingTotalSize;
        if (totalSize > 0 && self._totalSizeConfidence !== 'atLeast') {
            // Sanity Check that the size and offset combined are within bounds of the total page size
            if (newOffset + newSize > totalSize) {
                newSize = totalSize - newOffset;
            }
        }
        return new self.FetchByOffsetParameters(self, newOffset, newSize, params.sortCriteria, params.filterCriterion);
    }
    // helper method to fetch data from dataprovider
    // needs to make sure that no new mutation events have
    // occurred while fetching to prevent mistakes.
    _mutationEventDataFetcher(callback) {
        let self = this;
        this.dataProvider.getTotalSize().then(function (totalSize) {
            if (totalSize > 0) {
                self._mutatingTotalSize = totalSize;
                if (self._offset >= totalSize) {
                    self._offset = totalSize - ((totalSize - 1) % self._pageSize) - 1;
                    self._endItemIndex = totalSize - 1;
                }
            }
            self
                ._getCurrentPageData()
                .then(function (result) {
                if (self._mustRefetch) {
                    self._mustRefetch = false;
                    self._hasMutated = true;
                    self._mutationEventDataFetcher(callback);
                }
                else {
                    callback(result);
                }
            })
                .catch(function (reject) {
                // try again if must refetch is true
                if (self._mustRefetch) {
                    self._mustRefetch = false;
                    self._hasMutated = true;
                    self._mutationEventDataFetcher(callback);
                }
                else {
                    return Promise.reject(reject);
                }
            });
        });
    }
    // helper method to handle mutation events with index
    _processMutationEventsByKey(result) {
        let self = this;
        let removeMetadataArray = [];
        let removeDataArray = [];
        let removeIndexArray = [];
        let removeKeySet = new Set();
        let addMetadataArray = [];
        let addDataArray = [];
        let addIndexArray = [];
        let addKeySet = new Set();
        let updateMetadataArray = [];
        let updateDataArray = [];
        let updateIndexArray = [];
        let updateKeySet = new Set();
        //TODO iterate through previous page data vs current page data
        // and generate the mutation event detail
        let previousPageData = self._currentResultsForMutation.map(function (item, index) {
            return { item: item, index: index };
        });
        let updatedPageData = result['results'].map(function (item, index) {
            return { item: item, index: index };
        });
        let previousPageDataKeys = previousPageData.map(function (item) {
            return item.item.metadata.key;
        });
        let updatedPageDataKeys = updatedPageData.map(function (item) {
            return item.item.metadata.key;
        });
        let removedItems = previousPageData.filter(function (item) {
            return updatedPageDataKeys.indexOf(item.item.metadata.key) < 0;
        });
        let addedItems = updatedPageData.filter(function (item) {
            return previousPageDataKeys.indexOf(item.item.metadata.key) < 0;
        });
        // Use filtered mutation event queue to track update events
        let updateMutationsIndices = self._mutationEventQueue
            .filter(function (item) {
            return !item.detail.add && !item.detail.remove && item.detail.update;
        })
            .map(function (item) {
            return item.detail.update.indexes;
        });
        // flatten array so we can check for duplicates
        updateMutationsIndices = updateMutationsIndices.reduce(function (a, b) {
            return a.concat(b);
        }, []);
        // remove duplicate update entries
        updateMutationsIndices = updateMutationsIndices.filter(function (item, index) {
            return updateMutationsIndices.indexOf(item) === index;
        });
        // Use tracked update event items to check whether or not
        // existing items in the previous page data that are in the
        // same place in the current page date have updated.
        let updateItems = previousPageData.filter(function (item) {
            let updatedIndex = updatedPageDataKeys.indexOf(item.item.metadata.key);
            if (updateMutationsIndices.indexOf(updatedIndex) > -1) {
                return true;
            }
            return false;
        });
        // clear mutation event queue
        self._mutationEventQueue = [];
        if (addedItems.length > 0) {
            addedItems.forEach(function (item) {
                addMetadataArray.push(updatedPageData[item.index].item.metadata);
                addDataArray.push(updatedPageData[item.index].item.data);
                addIndexArray.push(item.index);
            });
            addMetadataArray.map(function (metadata) {
                addKeySet.add(metadata.key);
            });
        }
        if (removedItems.length > 0) {
            removedItems.forEach(function (item) {
                removeMetadataArray.push(previousPageData[item.index].item.metadata);
                removeDataArray.push(previousPageData[item.index].item.data);
                removeIndexArray.push(item.index);
            });
            removeMetadataArray.map(function (metadata) {
                removeKeySet.add(metadata.key);
            });
        }
        if (updateItems.length > 0) {
            updateItems.forEach(function (item) {
                updateMetadataArray.push(previousPageData[item.index].item.metadata);
                updateDataArray.push(previousPageData[item.index].item.data);
                updateIndexArray.push(item.index);
            });
            updateMetadataArray.map(function (metadata) {
                updateKeySet.add(metadata.key);
            });
        }
        // bug 36520: Adding mutation offset tracker to support collection iterator
        // based mutation event handling. Expected behavior should be: on remove event
        // iterator is called and expected to provide the new rows to fill in the
        // removed gap when we need add events that are out of current viewport range
        // Table specific
        let currentViewport = self._endItemIndex - self._offset - removeIndexArray.length;
        let oocvAddIndexArray = addIndexArray.filter(function (index) {
            return index >= currentViewport;
        });
        // Update mutation offset values
        this._iteratorCacheMap.forEach(function (iteratorData, clientId) {
            iteratorData['mutationOffset'] = oocvAddIndexArray.length;
            this._iteratorCacheMap.set(clientId, iteratorData);
        }.bind(this));
        // build mutation event detail and fire if not null
        let operationAddEventDetail = null;
        let operationRemoveEventDetail = null;
        let operationUpdateEventDetail = null;
        if (addIndexArray.length > 0) {
            operationAddEventDetail = new self.DataProviderAddOperationEventDetail(self, addKeySet, null, null, addMetadataArray, addDataArray, addIndexArray);
        }
        if (removeIndexArray.length > 0) {
            operationRemoveEventDetail = new self.DataProviderOperationEventDetail(self, removeKeySet, removeMetadataArray, removeDataArray, removeIndexArray);
        }
        if (updateIndexArray.length > 0) {
            operationUpdateEventDetail = new self.DataProviderOperationEventDetail(self, updateKeySet, updateMetadataArray, updateDataArray, updateIndexArray);
        }
        if (operationAddEventDetail != null ||
            operationRemoveEventDetail != null ||
            operationUpdateEventDetail != null) {
            let mutationEventDetail = new self.DataProviderMutationEventDetail(self, operationAddEventDetail, operationRemoveEventDetail, operationUpdateEventDetail);
            self.dispatchEvent(new oj.DataProviderMutationEvent(mutationEventDetail));
        }
    }
    _addEventListeners(dataprovider) {
        let self = this;
        dataprovider.addEventListener(this._REFRESH, function (event) {
            // Treat it as a set page to 0 and refresh if not
            // directly after a mutation b/c mutation event trigger
            // refresh naturally. Natural refreshes need to update
            // totalSize and force fetching.
            if (!self._hasMutated) {
                self._hasMutated = true;
                // If refresh occurs, setPage should complete before
                // fetchFirst triggers. this will help resolve the order
                self._isInitialDataLoaded = new Promise(function (resolve) {
                    self._dataResolveFunc = resolve;
                });
                self._updateTotalSize().then(function () {
                    self.setPage(0, { pageSize: self._pageSize }).then(function () {
                        // if no data, set page skips refresh event dispatch,
                        // so we need to fire it off here to update the view with no data.
                        if (self._endItemIndex === 0) {
                            self.dispatchEvent(new oj.DataProviderRefreshEvent());
                        }
                    });
                });
            }
        });
        dataprovider.addEventListener(this._MUTATE, function (event) {
            self._mutationEventQueue.push(event);
            self._setupMutationBusyContext();
            if (self._isFetchingForMutation) {
                // if new mutation has come in, we need to refetch. Otherwise, we may have stale data.
                self._mustRefetch = true;
            }
            else {
                self._isFetchingForMutation = true;
                self._currentResultsForMutation = self._currentResults;
                // kick off new data fetch
                self._hasMutated = true;
                self._mutationEventDataFetcher(function (result) {
                    // finish fetching updated data
                    self._isFetchingForMutation = false;
                    self._updateTotalSize().then(function () {
                        self._mutatingTotalSize = null;
                        let length = result['results'].length;
                        if (length === 0) {
                            // no results so need to refresh since page has changed
                            self._mutationFunc(true);
                            self.setPage(self._currentPage, { pageSize: self._pageSize });
                        }
                        else {
                            // need to update endItemIndex
                            self._endItemIndex = self._offset + length - 1;
                            self._processMutationEventsByKey(result);
                            self._mutationFunc(true);
                        }
                    });
                });
            }
        });
    }
}
oj['PagingDataProviderView'] = PagingDataProviderView;
oj.PagingDataProviderView = PagingDataProviderView;
oj.EventTargetMixin.applyMixin(PagingDataProviderView);



/* jslint browser: true,devel:true*/
/**
 *
 * @export
 * @final
 * @class PagingDataProviderView
 * @implements oj.PagingModel
 * @implements DataProvider
 * @classdesc This class implements {@link DataProvider}.
 *            Wraps a {@link DataProvider} to be used with [PagingControl]{@link oj.PagingControl}.
 *            Supports PagingModel API.
 * @param {DataProvider} dataProvider the {@link DataProvider} to be wrapped.
 *                                      <p>This can be either any DataProvider or a wrapped
 *                                      DataSource with a TableDataSourceAdapter. Paging DataProvider View does
 *                                      not handle DataProviders with unknown total sizes.</p>
 * @ojsignature [{target: "Type",
 *               value: "class PagingDataProviderView<K, D> implements DataProvider<K, D>, PagingModel"},
 *               {target: "Type",
 *               value: "DataProvider<K, D>",
 *               for: "dataProvider"}]
 * @ojtsimport {module: "ojdataprovider", type: "AMD", imported: ["DataProvider", "SortCriterion", "FetchByKeysParameters",
 * "ContainsKeysResults","FetchByKeysResults","FetchByOffsetParameters","FetchByOffsetResults",
 * "FetchListResult","FetchListParameters"]}
 * @ojtsimport {module: "ojpagingmodel", type: "AMD", imported: ["PagingModel"]}
 * @ojtsmodule
 */

/**
 * @inheritdoc
 * @memberof PagingDataProviderView
 * @instance
 * @method
 * @name containsKeys
 */

/**
 * @inheritdoc
 * @memberof PagingDataProviderView
 * @instance
 * @method
 * @name fetchFirst
 */

/**
 * @inheritdoc
 * @memberof PagingDataProviderView
 * @instance
 * @method
 * @name fetchByKeys
 */

/**
 * @inheritdoc
 * @memberof PagingDataProviderView
 * @instance
 * @method
 * @name fetchByOffset
 */

/**
 * @inheritdoc
 * @memberof PagingDataProviderView
 * @instance
 * @method
 * @name getCapability
 */

/**
 * @inheritdoc
 * @memberof PagingDataProviderView
 * @instance
 * @method
 * @name getTotalSize
 */

/**
 * @inheritdoc
 * @memberof PagingDataProviderView
 * @instance
 * @method
 * @name isEmpty
 */

/**
 * @inheritdoc
 * @memberof PagingDataProviderView
 * @instance
 * @method
 * @name addEventListener
 */

/**
 * @inheritdoc
 * @memberof PagingDataProviderView
 * @instance
 * @method
 * @name removeEventListener
 */

/**
 * @inheritdoc
 * @memberof PagingDataProviderView
 * @instance
 * @method
 * @name dispatchEvent
 */

/**
 * Get the current page
 * @return {number} The current page
 * @export
 * @expose
 * @memberof PagingDataProviderView
 * @method
 * @name getPage
 * @instance
 */

/**
 * Set the current page. This will trigger a refresh event. During initialization
 * the refresh event is skipped.
 * @param {number} value The current page
 * @param {Object=} options Options<p>
 *                  pageSize: The page size.<p>
 * @return {Promise} promise object triggering done when complete..
 * @export
 * @expose
 * @memberof PagingDataProviderView
 * @method
 * @name setPage
 * @instance
 */

/**
 * Get the current page start index
 * @return {number} The current page start index
 * @export
 * @expose
 * @memberof PagingDataProviderView
 * @method
 * @name getStartItemIndex
 * @instance
 */

/**
 * Get the current page end index
 * @return {number} The current page end index
 * @export
 * @expose
 * @memberof PagingDataProviderView
 * @method
 * @name getEndItemIndex
 * @instance
 */

/**
 * Get the page count
 * @return {number} The total number of pages
 * @export
 * @expose
 * @memberof PagingDataProviderView
 * @method
 * @name getPageCount
 * @instance
 */

/**
 * @export
 * Return the total number of items. Returns -1 if unknown.
 * @returns {number} total number of items
 * @expose
 * @memberof PagingDataProviderView
 * @method
 * @name totalSize
 * @instance
 */

/**
 * Returns the confidence for the totalSize value.
 * @return {string} "actual" if the totalSize is the time of the fetch is an exact number
 *                  "estimate" if the totalSize is an estimate
 *                  "atLeast" if the totalSize is at least a certain number
 *                  "unknown" if the totalSize is unknown
 * @export
 * @expose
 * @memberof PagingDataProviderView
 * @method
 * @name totalSizeConfidence
 * @instance
 */

/**
 * Translates and returns the global index given a local index.
 *
 * @param {number} value The local index to be translated
 * @return {number} The translated global index
 * @export
 * @expose
 * @memberof PagingDataProviderView
 * @method
 * @name getGlobalIndex
 * @instance
 */

/**
 * End of jsdoc
 */

  return PagingDataProviderView;
});