package cz.vitlabuda.icanteenjidelnicek;

/*
SPDX-License-Identifier: BSD-3-Clause

Copyright (c) 2021 Vít Labuda. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:
 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
    disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other materials provided with the distribution.
 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
    products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import android.app.Application;

import java.util.TreeMap;

public class App extends Application {
    public static final int CANTEEN_WEBSITE_CONNECTION_TIMEOUT = 5000; // in milliseconds

    // TreeMap is always sorted by key (= canteen name) alphabetically.
    public static final TreeMap<String, String> PREDEFINED_CANTEEN_MAP;

    static {
        PREDEFINED_CANTEEN_MAP = new TreeMap<>();

        PREDEFINED_CANTEEN_MAP.put("Gymnázium a OA Svitavy", "https://strav.nasejidelna.cz/0051/login");
        PREDEFINED_CANTEEN_MAP.put("Internátní školní jídelna Svitavy", "https://strav.nasejidelna.cz/0057/login");
        PREDEFINED_CANTEEN_MAP.put("ZŠ Felberova Svitavy", "http://82.117.143.136:8082/faces/login.jsp");
        PREDEFINED_CANTEEN_MAP.put("ZŠ náměstí Míru Svitavy", "https://strav.nasejidelna.cz/0057/login");
        PREDEFINED_CANTEEN_MAP.put("ZŠ Riegrova Svitavy", "https://strav.nasejidelna.cz/0057/login");
        PREDEFINED_CANTEEN_MAP.put("ZŠ Sokolovská Svitavy", "https://strav.nasejidelna.cz/0051/login");
        PREDEFINED_CANTEEN_MAP.put("ZŠ T. G. Masaryka Svitavy", "https://strav.nasejidelna.cz/0120/login");
    }
}
