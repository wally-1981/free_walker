package com.free.walker.service.itinerary.rest;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.basic.SecurityPolicy;
import com.free.walker.service.itinerary.basic.SecurityPolicy.SecurityLevel;
import com.free.walker.service.itinerary.dao.AccountDAO;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidAccountException;
import com.free.walker.service.itinerary.primitive.AccountStatus;
import com.free.walker.service.itinerary.primitive.Introspection;

/**
 * <b>AccountService</b> provides support for account management.<br>
 * <br>
 * This service supports consuming and producing data in below listed MIME
 * types:
 * <ul>
 * <li>application/json
 * </ul>
 */
@Path("/service/account/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountService {
    private AccountDAO accountDAO;

    public AccountService(Class<?> accountDaoClass) {
        accountDAO = DAOFactory.getAccountDAO(accountDaoClass.getName());
    }

    @POST
    @Path("/accounts/")
    @SecurityPolicy(level = SecurityLevel.HIGH)
    public Response addAccount(JsonObject account) {
        try {
            Account createdAccount = accountDAO.registerAccount(new Account().newFromJSON(account));
            return Response.ok(createdAccount.toJSON(true)).build();
        } catch (InvalidAccountException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @PUT
    @Path("/accounts/")
    @RequiresPermissions("ModifyAccount")
    @SecurityPolicy(level = SecurityLevel.HIGH)
    public Response updateAccount(JsonObject account) {
        try {
            Account updatedAccount = accountDAO.modifyAccount(new Account().fromJSON(account));
            return Response.ok(updatedAccount.toJSON(true)).build();
        } catch (InvalidAccountException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @PUT
    @Path("/accounts/{principle}")
    @RequiresRoles(value = {"customer", "agency"}, logical = Logical.OR)
    @RequiresPermissions("ChangePassword")
    @SecurityPolicy(level = SecurityLevel.HIGH)
    public Response changeAccountPassword(@PathParam("principle") String principle, JsonObject account) {
        try {
            String currentPassword = account.getString(Introspection.JSONKeys.ORIGINAL);
            String newPassword = account.getString(Introspection.JSONKeys.PASSWORD);
            accountDAO.changePassword(principle, currentPassword, newPassword);
            return Response.ok().build();
        } catch (InvalidAccountException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @PUT
    @Path("/accounts/{principle}/{statusId}")
    @RequiresPermissions("ModifyAccount")
    @SecurityPolicy(level = SecurityLevel.HIGH)
    public Response updateAccountStatus(@PathParam("principle") String principle, @PathParam("statusId") int statusId) {
        try {
            AccountStatus status = AccountStatus.valueOf(statusId);
            Account updatedAccount = null;
            if (AccountStatus.ACTIVE.equals(status)) {
                updatedAccount = accountDAO.activateAccount(principle);
            } else if (AccountStatus.LOCKED.equals(status)) {
                updatedAccount = accountDAO.lockAccount(principle);
            } else if (AccountStatus.IN_ACTIVE.equals(status)) {
                updatedAccount = accountDAO.deactivateAccount(principle);
            } else if (AccountStatus.REVOKED.equals(status)) {
                updatedAccount = accountDAO.revokeAccount(principle);
            } else {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.STATUS, statusId).build();
                return Response.status(Status.BAD_REQUEST).entity(res).build();
            }
            return Response.ok(updatedAccount.toJSON(true)).build();
        } catch (InvalidAccountException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @GET
    @Path("/accounts/{principle}")
    @RequiresPermissions("RetrieveAccount")
    @SecurityPolicy(level = SecurityLevel.HIGH)
    public Response retrieveAccount(@PathParam("principle") String principle) {
        try {
            return Response.ok(accountDAO.retrieveAccount(principle).toJSON(true)).build();
        } catch (InvalidAccountException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }
}
