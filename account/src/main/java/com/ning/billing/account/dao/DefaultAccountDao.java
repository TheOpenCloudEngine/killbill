/*
 * Copyright 2010-2011 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.account.dao;

import java.util.List;
import java.util.UUID;
import org.skife.jdbi.v2.IDBI;
import org.skife.jdbi.v2.Transaction;
import org.skife.jdbi.v2.TransactionStatus;
import org.skife.jdbi.v2.exceptions.TransactionFailedException;
import com.google.inject.Inject;
import com.ning.billing.ErrorCode;
import com.ning.billing.account.api.Account;
import com.ning.billing.account.api.AccountApiException;
import com.ning.billing.account.api.AccountChangeNotification;
import com.ning.billing.account.api.AccountCreationNotification;
import com.ning.billing.account.api.user.DefaultAccountChangeNotification;
import com.ning.billing.account.api.user.DefaultAccountCreationEvent;
import com.ning.billing.util.customfield.CustomField;
import com.ning.billing.util.customfield.dao.FieldStoreDao;
import com.ning.billing.util.eventbus.EventBus;
import com.ning.billing.util.tag.Tag;
import com.ning.billing.util.tag.dao.TagStoreSqlDao;

public class DefaultAccountDao implements AccountDao {
    private final AccountSqlDao accountDao;
    private final EventBus eventBus;

    @Inject
    public DefaultAccountDao(final IDBI dbi, final EventBus eventBus) {
        this.eventBus = eventBus;
        this.accountDao = dbi.onDemand(AccountSqlDao.class);
    }

    @Override
    public Account getAccountByKey(final String key) {
        return accountDao.inTransaction(new Transaction<Account, AccountSqlDao>() {
            @Override
            public Account inTransaction(final AccountSqlDao accountSqlDao, final TransactionStatus status) throws Exception {
                Account account = accountSqlDao.getAccountByKey(key);
                if (account != null) {
                    setCustomFieldsFromWithinTransaction(account, accountSqlDao);
                    setTagsFromWithinTransaction(account, accountSqlDao);
                }
                return account;
            }
        });
    }

    @Override
    public UUID getIdFromKey(final String externalKey) throws AccountApiException {
        if (externalKey == null) {
            throw new AccountApiException(ErrorCode.ACCOUNT_CANNOT_MAP_NULL_KEY, "");
        }
        return accountDao.getIdFromKey(externalKey);
    }

    @Override
    public Account getById(final String id) {
        return accountDao.inTransaction(new Transaction<Account, AccountSqlDao>() {
            @Override
            public Account inTransaction(final AccountSqlDao accountSqlDao, final TransactionStatus status) throws Exception {
                Account account = accountSqlDao.getById(id);
                if (account != null) {
                    setCustomFieldsFromWithinTransaction(account, accountSqlDao);
                    setTagsFromWithinTransaction(account, accountSqlDao);
                }
                return account;
            }
        });
    }


    @Override
    public List<Account> get() {
        return accountDao.get();
    }

    @Override
    public void create(final Account account) {
        final String key = account.getExternalKey();

        accountDao.inTransaction(new Transaction<Void, AccountSqlDao>() {
            @Override
            public Void inTransaction(final AccountSqlDao accountSqlDao, final TransactionStatus status) throws Exception {
                Account currentAccount = accountSqlDao.getAccountByKey(key);
                if (currentAccount != null) {
                    throw new AccountApiException(ErrorCode.ACCOUNT_ALREADY_EXISTS, key);
                }
                accountSqlDao.create(account);

                saveTagsFromWithinTransaction(account, accountSqlDao, true);
                saveCustomFieldsFromWithinTransaction(account, accountSqlDao, true);

                AccountCreationNotification creationEvent = new DefaultAccountCreationEvent(account);
                eventBus.post(creationEvent);
                return null;
            }
        });
    }

    @Override
    public void update(final Account account) throws AccountApiException {
        try {
            accountDao.inTransaction(new Transaction<Void, AccountSqlDao>() {
                @Override
                public Void inTransaction(final AccountSqlDao accountSqlDao, final TransactionStatus status) throws AccountApiException, EventBus.EventBusException {
                    String accountId = account.getId().toString();
                    Account currentAccount = accountSqlDao.getById(accountId);
                    if (currentAccount == null) {
                        throw new AccountApiException(ErrorCode.ACCOUNT_DOES_NOT_EXIST_FOR_ID, accountId);
                    }

                    String currentKey = currentAccount.getExternalKey();
                    if (!currentKey.equals(account.getExternalKey())) {
                        throw new AccountApiException(ErrorCode.ACCOUNT_CANNOT_CHANGE_EXTERNAL_KEY, currentKey);
                    }

                    accountSqlDao.update(account);

                    saveTagsFromWithinTransaction(account, accountSqlDao, false);
                    saveCustomFieldsFromWithinTransaction(account, accountSqlDao, false);

                    AccountChangeNotification changeEvent = new DefaultAccountChangeNotification(account.getId(), currentAccount, account);
                    if (changeEvent.hasChanges()) {
                        eventBus.post(changeEvent);
                    }
                    return null;
                }
            });
        } catch (RuntimeException t) {
            if (t.getCause() instanceof AccountApiException) {
                throw (AccountApiException) t.getCause();
            } else {
                throw t;
            }
        }
    }

    @Override
    public void test() {
        accountDao.test();
    }

    private void setCustomFieldsFromWithinTransaction(final Account account, final AccountSqlDao transactionalDao) {
        FieldStoreDao fieldStoreDao = transactionalDao.become(FieldStoreDao.class);
        List<CustomField> fields = fieldStoreDao.load(account.getId().toString(), account.getObjectName());

        account.clearFields();
        if (fields != null) {
            account.addFields(fields);
        }
    }

    private void setTagsFromWithinTransaction(final Account account, final AccountSqlDao transactionalDao) {
        TagStoreSqlDao tagStoreDao = transactionalDao.become(TagStoreSqlDao.class);
        List<Tag> tags = tagStoreDao.load(account.getId().toString(), account.getObjectName());
        account.clearTags();

        if (tags != null) {
            account.addTags(tags);
        }
    }

    private void saveCustomFieldsFromWithinTransaction(final Account account, final AccountSqlDao transactionalDao, final boolean isCreation) {
        String accountId = account.getId().toString();
        String objectType = account.getObjectName();

        TagStoreSqlDao tagStoreDao = transactionalDao.become(TagStoreSqlDao.class);
        if (!isCreation) {
            tagStoreDao.clear(accountId, objectType);
        }

        List<Tag> tagList = account.getTagList();
        if (tagList != null) {
            tagStoreDao.save(accountId, objectType, tagList);
        }
    }

    private void saveTagsFromWithinTransaction(final Account account, final AccountSqlDao transactionalDao, final boolean isCreation) {
        String accountId = account.getId().toString();
        String objectType = account.getObjectName();

        FieldStoreDao fieldStoreDao = transactionalDao.become(FieldStoreDao.class);
        if (!isCreation) {
            fieldStoreDao.clear(accountId, objectType);
        }

        List<CustomField> fieldList = account.getFieldList();
        if (fieldList != null) {
            fieldStoreDao.save(accountId, objectType, fieldList);
        }
    }
}
