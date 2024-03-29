/*
 * Copyright 2021 Carlos Conyers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package little.cli

import org.apache.commons.cli.*

/** `Option` and `OptionGroup` can added to `Options`. */
type Optionable = Option | OptionGroup

private[cli] def mergeOptions(options: Options, opts: Seq[Optionable]): Options =
  opts.foldLeft(options) { (_, opt) =>
    opt match
      case opt: Option      => options.addOption(opt)
      case opt: OptionGroup => options.addOptionGroup(opt)
  }
