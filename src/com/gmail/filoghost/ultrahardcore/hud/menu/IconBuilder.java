/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.ultrahardcore.hud.menu;

import java.util.List;

import org.bukkit.Material;

import wild.api.menu.ClickHandler;
import wild.api.menu.Icon;

public class IconBuilder {

	Icon icon;
	
	public IconBuilder(Material mat) {
		icon = new Icon(mat);
	}
	
	public IconBuilder name(String name) {
		icon.setName(name);
		return this;
	}
	
	
	public IconBuilder lore(List<String> lore) {
		icon.setLore(lore);
		return this;
	}
	
	public IconBuilder amount(int amount) {
		icon.setAmount(amount);
		return this;
	}
	
	public IconBuilder dataValue(int dataValue) {
		icon.setDataValue((short) dataValue);
		return this;
	}
	
	public IconBuilder clickHandler(ClickHandler clickHandler) {
		icon.setClickHandler(clickHandler);
		return this;
	}

	public IconBuilder closeOnClick(boolean closeOnClick) {
		icon.setCloseOnClick(closeOnClick);
		return this;
	}
	
	public Icon build() {
		return icon;
	}
	
}
