/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.webtrans.server.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.management.JpaIdentityStore;
import org.zanata.dao.AccountDAO;
import org.zanata.model.HAccount;
import org.zanata.model.HAccountOption;
import org.zanata.webtrans.client.presenter.UserConfigHolder;
import org.zanata.webtrans.server.ActionHandlerFor;
import org.zanata.webtrans.shared.model.UserOptions;
import org.zanata.webtrans.shared.rpc.LoadOptionsAction;
import org.zanata.webtrans.shared.rpc.LoadOptionsResult;
import org.zanata.webtrans.shared.rpc.NavOption;


/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Name("webtrans.gwt.LoadOptionsHandler")
@Scope(ScopeType.STATELESS)
@ActionHandlerFor(LoadOptionsAction.class)
public class LoadOptionsHandler extends AbstractActionHandler<LoadOptionsAction, LoadOptionsResult>
{
   @In(value = JpaIdentityStore.AUTHENTICATED_USER)
   private HAccount authenticatedAccount;

   @In
   private AccountDAO accountDAO;


   @Override
   public LoadOptionsResult execute(LoadOptionsAction action, ExecutionContext context) throws ActionException
   {
      UserConfigHolder configHolder = new UserConfigHolder();
      HAccount account = accountDAO.findById(authenticatedAccount.getId(), true);
      Map<String,HAccountOption> options = account.getEditorOptions();
      HashMap<String, HAccountOption> filteredOptions = new HashMap<String, HAccountOption>();

      if (action.getPrefixes() != null && !action.getPrefixes().isEmpty())
      {
         for (Entry<String, HAccountOption> entry : options.entrySet())
         {
            for (String prefix : action.getPrefixes())
            {
               if (entry.getKey().startsWith(prefix))
               {
                  // filter config according to prefix
                  filteredOptions.put(entry.getKey(), entry.getValue());
               }
            }
         }
      }

      if (filteredOptions.containsKey(UserOptions.DisplayButtons.getPersistentName()))
      {
         configHolder.setDisplayButtons(filteredOptions.get(UserOptions.DisplayButtons.getPersistentName()).getValueAsBoolean());
      }

      if (filteredOptions.containsKey(UserOptions.EnterSavesApproved.getPersistentName()))
      {
         configHolder.setEnterSavesApproved(filteredOptions.get(UserOptions.EnterSavesApproved.getPersistentName()).getValueAsBoolean());
      }

      if (filteredOptions.containsKey(UserOptions.Navigation.getPersistentName()))
      {
         configHolder.setNavOption(NavOption.valueOf(filteredOptions.get(UserOptions.Navigation.getPersistentName()).getValue()));
      }

      if (filteredOptions.containsKey(UserOptions.EditorPageSize.getPersistentName()))
      {
         configHolder.setEditorPageSize(filteredOptions.get(UserOptions.EditorPageSize.getPersistentName()).getValueAsInt());
      }

      if (filteredOptions.containsKey(UserOptions.ShowErrors.getPersistentName()))
      {
         configHolder.setShowError(filteredOptions.get(UserOptions.ShowErrors.getPersistentName()).getValueAsBoolean());
      }

      if (filteredOptions.containsKey(UserOptions.TranslatedMessageFilter.getPersistentName()))
      {
         configHolder.setFilterByTranslated(filteredOptions.get(UserOptions.TranslatedMessageFilter.getPersistentName()).getValueAsBoolean());
      }

      if (filteredOptions.containsKey(UserOptions.NeedReviewMessageFilter.getPersistentName()))
      {
         configHolder.setFilterByNeedReview(filteredOptions.get(UserOptions.NeedReviewMessageFilter.getPersistentName()).getValueAsBoolean());
      }

      if (filteredOptions.containsKey(UserOptions.UntranslatedMessageFilter.getPersistentName()))
      {
         configHolder.setFilterByUntranslated(filteredOptions.get(UserOptions.UntranslatedMessageFilter.getPersistentName()).getValueAsBoolean());
      }

      if (filteredOptions.containsKey(UserOptions.DocumentListPageSize.getPersistentName()))
      {
         configHolder.setDocumentListPageSize(filteredOptions.get(UserOptions.DocumentListPageSize.getPersistentName()).getValueAsInt());
      }

      LoadOptionsResult result = new LoadOptionsResult();
      result.setConfiguration( configHolder.getState() );
      return result;
   }

   @Override
   public void rollback(LoadOptionsAction action, LoadOptionsResult result, ExecutionContext context) throws ActionException
   {
   }
}
