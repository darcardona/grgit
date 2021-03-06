/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Tag
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class TagAddOpSpec extends SimpleGitOpSpec {
    List commits = []

    def setup() {
        repoFile('1.txt') << '1'
        commits << grgit.commit(message: 'do', all: true)

        repoFile('1.txt') << '2'
        commits << grgit.commit(message: 'do', all: true)

        repoFile('1.txt') << '3'
        commits << grgit.commit(message: 'do', all: true)
    }

    def 'tag add creates annotated tag pointing to current HEAD'() {
        when:
        grgit.tag.add(name: 'test-tag')
        then:
        grgit.tag.list() == [new Tag(
            commits[2],
            person,
            'refs/tags/test-tag',
            '',
            ''
        )]
        grgit.resolve.toCommit('test-tag') == grgit.head()
    }

    def 'tag add with annotate false creates unannotated tag pointing to current HEAD'() {
        when:
        grgit.tag.add(name: 'test-tag', annotate: false)
        then:
        grgit.tag.list() == [new Tag(
            commits[2],
            null,
            'refs/tags/test-tag',
            null,
            null
        )]
        grgit.resolve.toCommit('test-tag') == grgit.head()
    }

    def 'tag add with name and pointsTo creates tag pointing to pointsTo'() {
        when:
        grgit.tag.add(name: 'test-tag', pointsTo: commits[0].id)
        then:
        grgit.tag.list() == [new Tag(
            commits[0],
            person,
            'refs/tags/test-tag',
            '',
            ''
        )]
        grgit.resolve.toCommit('test-tag') == commits[0]
    }

    def 'tag add without force fails to overwrite existing tag'() {
        given:
        grgit.tag.add(name: 'test-tag', pointsTo: commits[0].id)
        when:
        grgit.tag.add(name: 'test-tag')
        then:
        thrown(GrgitException)
    }

    def 'tag add with force overwrites existing tag'() {
        given:
        grgit.tag.add(name: 'test-tag', pointsTo: commits[0].id)
        when:
        grgit.tag.add(name: 'test-tag', force: true)
        then:
        grgit.resolve.toCommit('test-tag') == grgit.head()
    }
}
