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
package com.gmail.filoghost.ultrahardcore.utils.scatter;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class SquareSpotsGenerator {
	
	private int startSide;
	
	private Integer[] sideCoords;
	
	public SquareSpotsGenerator(int side) {
		startSide = side;
	}
	
	public XZ[] generate() {
		if (sideCoords == null) {
			
			sideCoords = new Integer[] {-startSide, startSide};
			
			return new XZ[]
				{
					new XZ(startSide, startSide),
					new XZ(startSide, -startSide),
					new XZ(-startSide, startSide),
					new XZ(-startSide, -startSide),
				};
		}
		
		Integer[] oldSideCoords = sideCoords;
		sideCoords = new Integer[oldSideCoords.length * 2 - 1];
		
		Integer[] addedCoords = new Integer[sideCoords.length - oldSideCoords.length];
		int index = 0;
		
		for (int i = 0; i < oldSideCoords.length; i++) {
			if (i * 2 < sideCoords.length) {
				sideCoords[i * 2] = oldSideCoords[i];
			}
		}
		
		for (int i = 0; i < sideCoords.length; i++) {
			if (sideCoords[i] == null) {
				if (i > 0 && i < sideCoords.length - 1) {
					sideCoords[i] = (sideCoords[i - 1] + sideCoords[i + 1]) / 2;
					addedCoords[index++] = sideCoords[i];
				}
			}
		}
		
		return generateSpecular(addedCoords);
	}
	
	private XZ[] generateSpecular(Integer[] side) {
		XZ[] specular = new XZ[side.length * 4];
		
		for (int i = 0; i < side.length; i++) {
			int value = side[i];
			specular[i * 4] = 		new XZ(startSide, value);
			specular[i * 4 + 1] = 	new XZ(- startSide, value);
			specular[i * 4 + 2] = 	new XZ(value, startSide);
			specular[i * 4 + 3] = 	new XZ(value, - startSide);
		}
		
		return specular;
	}
	
	@AllArgsConstructor
	@Getter
	public static class XZ {
		
		private int x, z;

		@Override
		public String toString() {
			return "XY[x=" + x + ", z=" + z + "]";
		}
	}
	
}
