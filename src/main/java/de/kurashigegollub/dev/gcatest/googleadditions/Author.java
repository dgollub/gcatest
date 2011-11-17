/**
 * Copyright (C) 2011 Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 * Please see the README file for details.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.kurashigegollub.dev.gcatest.googleadditions;

import com.google.api.client.util.Key;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 */
public class Author {
    @Key
    public String name;
    @Key
    public String email;
}
