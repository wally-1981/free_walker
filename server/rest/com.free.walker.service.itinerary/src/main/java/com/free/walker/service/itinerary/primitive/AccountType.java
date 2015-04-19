package com.free.walker.service.itinerary.primitive;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.permission.AllPermission;

public enum AccountType {
    ADMIN, MASTER, AGENCY, WeChat, QQ, AliPay, WeiBo;

    public static AccountType valueOf(int ordinal) {
        if (ordinal == ADMIN.ordinal()) {
            return ADMIN;
        } else if (ordinal == MASTER.ordinal()) {
            return MASTER;
        } else if (ordinal == AGENCY.ordinal()) {
            return AGENCY;
        } else if (ordinal == WeChat.ordinal()) {
            return WeChat;
        } else if (ordinal == QQ.ordinal()) {
            return QQ;
        } else if (ordinal == AliPay.ordinal()) {
            return AliPay;
        } else if (ordinal == WeiBo.ordinal()) {
            return WeiBo;
        } else {
            return null;
        }
    }

    public static boolean isTouristAccount(AccountType accountType) {
        if (accountType == ADMIN) {
            return false;
        } else if (accountType == MASTER) {
            return true;
        } else if (accountType == AGENCY) {
            return false;
        } else if (accountType == WeChat) {
            return true;
        } else if (accountType == QQ) {
            return true;
        } else if (accountType == AliPay) {
            return true;
        } else if (accountType == WeiBo) {
            return true;
        } else {
            return false;
        }
    }

    public static Role getDefaultRole(AccountType accountType) {
        if (accountType == ADMIN) {
            return Role.ADMIN;
        } else if (accountType == MASTER) {
            return Role.CUSTOMER;
        } else if (accountType == AGENCY) {
            return Role.AGENCY;
        } else if (accountType == WeChat) {
            return Role.CUSTOMER;
        } else if (accountType == QQ) {
            return Role.CUSTOMER;
        } else if (accountType == AliPay) {
            return Role.CUSTOMER;
        } else if (accountType == WeiBo) {
            return Role.CUSTOMER;
        } else {
            return Role.CUSTOMER;
        }
    }

    public static enum Role {
        ROOT,
        ADMIN,
        AGENCY,
        CUSTOMER;

        public static Role valueOf(int ordinal) {
            if (ordinal == ROOT.ordinal()) {
                return ROOT;
            } else if (ordinal == ADMIN.ordinal()) {
                return ADMIN;
            } else if (ordinal == AGENCY.ordinal()) {
                return AGENCY;
            } else if (ordinal == CUSTOMER.ordinal()) {
                return CUSTOMER;
            } else {
                return null;
            }
        }

        public static org.apache.shiro.authz.Permission[] getDefaultPermissions(Role role) {
            if (role == ROOT) {
                return new org.apache.shiro.authz.Permission[] { new AllPermission() };
            } else if (role == ADMIN) {
                return Permission.ADMIN_PERMISSIONS;
            } else if (role == AGENCY) {
                return Permission.AGENCY_PERMISSIONS;
            } else if (role == CUSTOMER) {
                return Permission.CUSTOMER_PERMISSIONS;
            } else {
                return new Permission[0];
            }
        }
    }

    public static enum Permission implements org.apache.shiro.authz.Permission  {
        ChangePassword,
        CreateProduct,
        CreateProposal,
        GrabProposal,
        ManagePlatform,
        ModifyAccount,
        ModifyProduct,
        ModifyProposal,
        PublishProduct,
        RetrieveAccount,
        RetrieveProduct,
        RetrieveProposal,
        RetrieveTag,
        SearchProposal,
        SubmitProduct,
        SubmitProposal,
        UnpublishProduct;

        public static Permission[] ADMIN_PERMISSIONS = new Permission[] {
            ManagePlatform,
            SearchProposal,
            UnpublishProduct,
            RetrieveProduct,
            RetrieveProposal,
            RetrieveTag,
            ModifyAccount,
            RetrieveAccount
        };

        public static Permission[] AGENCY_PERMISSIONS = new Permission[] {
            CreateProduct,
            ModifyProduct,
            SubmitProduct,
            GrabProposal,
            PublishProduct,
            RetrieveProduct,
            RetrieveProposal,
            RetrieveTag,
            ModifyAccount,
            ChangePassword,
            RetrieveAccount
        };

        public static Permission[] CUSTOMER_PERMISSIONS = new Permission[] {
            CreateProposal,
            ModifyProposal,
            SubmitProposal,
            PublishProduct,
            RetrieveProduct,
            RetrieveProposal,
            RetrieveTag,
            ModifyAccount,
            ChangePassword,
            RetrieveAccount
        };

        public static List<org.apache.shiro.authz.Permission> valueOfPermissions(String[] permissions) {
            List<org.apache.shiro.authz.Permission> results = new ArrayList<org.apache.shiro.authz.Permission>();
            for (int i = 0; i < permissions.length && Permission.valueOf(permissions[i]) != null; i++) {
                results.add(Permission.valueOf(permissions[i]));
            }
            return results;
        }

        public boolean implies(org.apache.shiro.authz.Permission p) {
            return this.equals(p);
        }
    }
}
