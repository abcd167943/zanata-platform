// @ts-nocheck
import React from 'react'
import { Component } from 'react'
import * as PropTypes from 'prop-types'
import { includes } from 'lodash'
import DeleteEntry from './DeleteEntry'
import { Loader, Icon }
 from '../../components'
import Tag from 'antd/lib/tag'
import 'antd/lib/tag/style/css'
import { getLanguageUrl } from '../../utils/UrlHelper'

class Entry extends Component {
  static propTypes = {
    userLanguageTeams: PropTypes.object,
    locale: PropTypes.object.isRequired,
    permission: PropTypes.object.isRequired,
    isDeleting: PropTypes.bool,
    handleDelete: PropTypes.func
  }

  constructor () {
    super()
    this.state = {
      showDeleteModal: false
    }
  }

  setShowingDeleteEntryModal = (showing) => {
    this.setState({
      showDeleteModal: showing
    })
  }

  render () {
    const {
      userLanguageTeams,
      locale,
      permission,
      isDeleting,
      handleDelete
    } = this.props

    const localeDetails = locale.localeDetails
    /* eslint-disable react/jsx-no-bind */
    const isUserInTeam = includes(userLanguageTeams, localeDetails.localeId)

    const url = getLanguageUrl(localeDetails.localeId)

    return (
      <tr name='language-entry'>
        <td>
          <a href={url} id={'language-name-' + localeDetails.localeId}>
            <span name='language-name'>
              {localeDetails.localeId} [{localeDetails.nativeName}]
            </span>
            {localeDetails.enabledByDefault &&
              <Tag color='03A6D7'>
                DEFAULT
              </Tag>
            }
            {!localeDetails.enabled &&
              <Tag color='cyan'>
                DISABLED
              </Tag>
            }
            {isUserInTeam &&
              <Tag color='62c876'>
                Member
              </Tag>
            }
          </a>
          <br />
          <span className='languageCode'>
            {localeDetails.displayName}
          </span>
        </td>
        <td>
          <span className='txt-muted'>
            <Icon name='user' className='s1 mr1' />
            {locale.memberCount} &nbsp;
            {permission.canAddLocale &&
              <span className='ml2'>
                <Icon name='notification' className='s1 mr1' />
                {locale.requestCount}
              </span>
            }
          </span>
        </td>
        {permission.canDeleteLocale &&
          <td>
            {isDeleting
              ? <Loader />
              : <DeleteEntry locale={localeDetails}
                isDeleting={false}
                show={this.state.showDeleteModal}
                handleDeleteEntryDisplay={(display) =>
                    this.setShowingDeleteEntryModal(display)}
                handleDeleteEntry={handleDelete} />
            }
          </td>
        }
      </tr>
    )
    /* eslint-disable react/jsx-no-bind */
  }
}

export default Entry
