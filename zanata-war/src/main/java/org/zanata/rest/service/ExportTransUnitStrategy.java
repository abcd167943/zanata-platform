/*
 * Copyright 2013, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.zanata.rest.service;

import static net.sf.okapi.common.LocaleId.fromBCP47;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.sf.okapi.common.resource.ITextUnit;

import org.zanata.model.tm.TransMemoryUnitVariant;
import org.zanata.model.tm.TransMemoryUnit;

/**
 * Writes one or more variations for a single TransMemoryUnit as a TMX translation unit.
 * @author Sean Flanigan <a href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 *
 */
@ParametersAreNonnullByDefault
public class ExportTransUnitStrategy extends AbstractExportTUStrategy<TransMemoryUnit>
{
   public ExportTransUnitStrategy()
   {
      super();
   }

   private String resolveSourceLanguage(TransMemoryUnit tu)
   {
      String tuSourceLanguage = tu.getSourceLanguage();
      if (tuSourceLanguage != null)
      {
         return tuSourceLanguage;
      }
      else
      {
         String tmSourceLanguage = tu.getTranslationMemory().getSourceLanguage();
         assert tmSourceLanguage != null;
         return tmSourceLanguage;
      }

   }

   @Override
   protected @Nonnull String getSrcContent(TransMemoryUnit tu)
   {
      TransMemoryUnitVariant tuv = tu.getTransUnitVariants().get(resolveSourceLanguage(tu));
      assert tuv != null;
      return tuv.getPlainTextSegment();
   }

   @Override
   protected @Nonnull net.sf.okapi.common.LocaleId getSourceLocale(TransMemoryUnit tu)
   {
      return fromBCP47(resolveSourceLanguage(tu));
   }

   @Override
   protected @Nullable String getTUID(TransMemoryUnit tu)
   {
      return tu.getTransUnitId();
   }

   @Override
   protected void addTextUnitVariants(ITextUnit textUnit, TransMemoryUnit tu)
   {
      for (TransMemoryUnitVariant tuv: tu.getTransUnitVariants().values())
      {
         addTUV(textUnit, tuv);
      }
   }

   private void addTUV(ITextUnit textUnit, TransMemoryUnitVariant tuv)
   {
      @Nonnull String trgContent = tuv.getPlainTextSegment();
      @Nonnull net.sf.okapi.common.LocaleId locId = fromBCP47(tuv.getLanguage());
      addTargetToTextUnit(textUnit, locId, trgContent);
   }

}
