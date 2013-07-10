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
package org.zanata.model.tm;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.MapKeyClass;

import org.zanata.model.ModelEntityBase;
import org.zanata.util.HashUtil;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A translation unit variant.
 * This is the equivalent of a translated string.
 *
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Entity
@EqualsAndHashCode(exclude = {"content"})
@ToString(exclude = {"contentHash", "metadata"})
@NoArgsConstructor
@Access(AccessType.FIELD)
public class TMTransUnitVariant extends ModelEntityBase
{
   public enum TMTransUnitVariantMetadata
   {
      TMX_SEG;
   }

   @Getter @Setter
   @Column(nullable = false)
   private String language;

   @Getter
   @Column(nullable = false)
   private String content;

   @Getter @Setter(AccessLevel.PROTECTED)
   @Column(name ="content_hash", nullable = false)
   private String contentHash;

   @Getter @Setter
   @ElementCollection
   @MapKeyClass(TMTransUnitVariantMetadata.class)
   @JoinTable(name = "TMTransUnitVariant_Metadata")
   @Lob
   private Map<TMTransUnitVariantMetadata, String> metadata = new HashMap<TMTransUnitVariantMetadata, String>();

   public TMTransUnitVariant(String language, String content)
   {
      this.language = language;
      this.setContent(content);
   }

   public void setContent(String content)
   {
      this.content = content;
      updateContentHash();
   }

   private void updateContentHash()
   {
      this.contentHash = HashUtil.generateHash(this.content);
   }
}
